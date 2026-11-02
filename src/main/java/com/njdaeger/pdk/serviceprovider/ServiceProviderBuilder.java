package com.njdaeger.pdk.serviceprovider;

import com.njdaeger.pdk.utils.Pair;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceProviderBuilder {

    private final Map<Class<?>, Pair<Class<?>, Function<IServiceProvider, ?>>> transients = new HashMap<>();
    private final Map<Class<?>, Pair<Class<?>, Function<IServiceProvider, ?>>> singletons = new HashMap<>();
    private IServiceProvider parent = null;
    private String providerName = "";

    /**
     * Creates a new service provider builder.
     * @return The service provider builder.
     */
    public static ServiceProviderBuilder builder() {
        return new ServiceProviderBuilder();
    }

    /**
     * Creates a new service provider builder with a parent service provider. Every service in the parent that is not overridden in this service provider will be available to this service provider
     * @param parent The parent service provider.
     * @return The service provider builder.
     */
    public static ServiceProviderBuilder builder(IServiceProvider parent) {
        ServiceProviderBuilder builder = new ServiceProviderBuilder();
        builder.parent = parent;
        return builder;
    }

    /**
     * Creates a new service provider builder with a name.
     * @param providerName The name of the service provider.
     * @return The service provider builder.
     */
    public static ServiceProviderBuilder builder(String providerName) {
        var builder = new ServiceProviderBuilder();
        builder.providerName = providerName;
        return builder;
    }

    public static ServiceProviderBuilder builder(String providerName, IServiceProvider parent) {
        ServiceProviderBuilder builder = new ServiceProviderBuilder();
        builder.parent = parent;
        builder.providerName = providerName;
        return builder;
    }

    /**
     * Adds a singleton service to the service provider. This means that the service will only be instantiated once and will be shared across all classes that request it.
     * @param serviceInterfaceClass The interface of the service.
     * @param generatorFunction A function that will generate the service.
     * @param <S> The service interface
     * @param <I> The service implementation
     * @return The service provider builder.
     */
    public <S, I extends S> ServiceProviderBuilder addSingleton(Class<S> serviceInterfaceClass, Function<IServiceProvider, I> generatorFunction) {
        singletons.put(serviceInterfaceClass, Pair.of(null, generatorFunction));
        return this;
    }

    /**
     * Adds a singleton service to the service provider. This means that the service will only be instantiated once and will be shared across all classes that request it.
     * @param serviceInterfaceClass The interface of the service.
     * @param serviceImplementationClass The implementation of the service.
     * @param <S> The service interface
     * @param <I> The service implementation
     * @return The service provider builder.
     */
    public <S, I extends S> ServiceProviderBuilder addSingleton(Class<S> serviceInterfaceClass, Class<I> serviceImplementationClass) {
        singletons.put(serviceInterfaceClass, Pair.of(serviceImplementationClass, generationFunction(serviceImplementationClass)));
        return this;
    }

    /**
     * Adds a transient service to the service provider. This means that the service will be instantiated every time it is requested.
     * @param serviceInterfaceClass The interface of the service.
     * @param generatorFunction A function that will generate the service.
     * @param <S> The service interface
     * @return The service provider builder.
     */
    public <S> ServiceProviderBuilder addTransient(Class<S> serviceInterfaceClass, Function<IServiceProvider, S> generatorFunction) {
        transients.put(serviceInterfaceClass, Pair.of(null, generatorFunction));
        return this;
    }

    /**
     * Adds a transient service to the service provider. This means that the service will be instantiated every time it is requested.
     * @param serviceInterfaceClass The interface of the service.
     * @param serviceImplementationClass The implementation of the service.
     * @param <S> The service interface
     * @param <I> The service implementation
     * @return The service provider builder.
     */
    public <S, I extends S> ServiceProviderBuilder addTransient(Class<S> serviceInterfaceClass, Class<I> serviceImplementationClass) {
        transients.put(serviceInterfaceClass, Pair.of(serviceImplementationClass, generationFunction(serviceImplementationClass)));
        return this;
    }

    /**
     * Builds the service provider.
     * @param plugin The plugin instance.
     * @return The built service provider.
     */
    public IServiceProvider build(Plugin plugin) {
        return new ServiceProvider(plugin, providerName, orderSingletonsInLoadOrder(), orderTransientsInLoadOrder(), parent);
    }

    /**
     * Orders the transients in load order. This is done by checking the constructors of each scope and seeing if all the parameters can be loaded from the singletons or scopes that will have already been loaded.
     * @return  A map of scopes in load order.
     */
    private Map<Class<?>, Function<IServiceProvider, ?>> orderTransientsInLoadOrder() {
        var merged = new HashSet<>(singletons.keySet());
        merged.addAll(transients.keySet());
        if (parent != null) {
            merged.addAll(parent.getSingletons().keySet());
            merged.addAll(parent.getTransients().keySet());
        }
        var ordered = new HashMap<Class<?>, Function<IServiceProvider, ?>>();
        var toLoad = new Stack<Class<?>>();
        toLoad.addAll(transients.keySet());

        Class<?> recursiveIndicator = null;
        while (!toLoad.isEmpty()) {
            var intf = toLoad.pop();
            var pair = transients.get(intf);
            var impl = pair.getFirst();
            if (impl == null) {
                ordered.put(intf, pair.getSecond());
                continue;
            }
            var constructor = Arrays.stream(impl.getConstructors()).max(Comparator.comparingInt(Constructor::getParameterCount)).orElseThrow(() -> new RuntimeException("Cannot instantiate " + impl.getSimpleName() + " because it has no public constructors."));
            var params = constructor.getParameters();

            if (params.length == 0 || canBeLoaded(params, merged)) {
                ordered.put(intf, pair.getSecond());
            } else {
                //if we were unable to be loaded with currently loaded classes, check if we can be loaded AT ALL. Checking the transient classes here can show if we are missing a scoped dependency.
                if (!canBeLoaded(params, transients.keySet())) throw new RuntimeException("[" + providerName + "ServiceProviderBuilder] Cannot instantiate " + impl.getSimpleName() + " because there are missing transient dependencies. Unable to find " + Stream.of(params).map(Parameter::getType).filter(type -> !transients.containsKey(type)).map(Class::getSimpleName).reduce((s1, s2) -> s1 + ", " + s2).orElse("no dependencies") + ".");
                //if we have already tried to load this class and STILL failed, then we have a circular dependency.
                if (recursiveIndicator == intf) throw new RuntimeException("[" + providerName + "ServiceProviderBuilder] Cannot instantiate " + impl.getSimpleName() + " because it has a circular dependency.");
                toLoad.addFirst(intf);
                recursiveIndicator = intf;
            }
        }
        return ordered;
    }

    /**
     * Orders the singletons in load order. This is done by checking the constructors of each singleton and seeing if all the parameters can be loaded from the singletons that will have already been loaded.
     * @return A map of singletons in load order.
     */
    private Stack<Pair<Class<?>, Function<IServiceProvider, ?>>> orderSingletonsInLoadOrder() {
        var readyToLoad = new Stack<Pair<Class<?>, Function<IServiceProvider, ?>>>();
        var waitingToLoad = new Stack<Class<?>>();
        waitingToLoad.addAll(singletons.keySet());

        var goodToUse = new HashSet<>(transients.keySet());
        if (parent != null) {
            goodToUse.addAll(parent.getSingletons().keySet());
            goodToUse.addAll(parent.getTransients().keySet());
        }

        Class<?> recursiveIndicator = null;
        while (!waitingToLoad.isEmpty()) {
            var intf = waitingToLoad.pop();
            var pair = singletons.get(intf);
            var impl = pair.getFirst();
            if (impl == null) {
                readyToLoad.push(Pair.of(intf, pair.getSecond()));
                continue;
            }

            var constructor = Arrays.stream(impl.getConstructors()).max(Comparator.comparingInt(Constructor::getParameterCount)).orElseThrow(() -> new RuntimeException("Cannot instantiate " + impl.getSimpleName() + " because it has no public constructors."));
            var params = constructor.getParameters();
            var totalAvailable = Stream.concat(readyToLoad.stream().map(Pair::getFirst), goodToUse.stream()).collect(Collectors.toSet());
            //can be we loaded by what's available currently?
            if (canBeLoaded(params, totalAvailable)) {
                readyToLoad.push(Pair.of(intf, pair.getSecond()));
            } else {
                //can we be loaded by what's available currently + what is waiting to be loaded?
                var allServices = Stream.concat(totalAvailable.stream(), waitingToLoad.stream()).collect(Collectors.toSet());
                if (!canBeLoaded(params, allServices)) throw new RuntimeException("[" + providerName + "ServiceProviderBuilder] Cannot instantiate " + impl.getSimpleName() + " because there are missing singleton dependencies. Unable to find " + Stream.of(params).map(Parameter::getType).filter(type -> !allServices.contains(type)).map(Class::getSimpleName).reduce((s1, s2) -> s1 + ", " + s2).orElse("no dependencies") + ".");

                //if we have already tried to load this class and STILL failed, then we have a circular dependency.
                if (recursiveIndicator == intf) throw new RuntimeException("[" + providerName + "ServiceProviderBuilder] Cannot instantiate " + intf.getSimpleName() + " because it has a circular dependency.");

                //we will try to load this class again after we have loaded the other singletons that are waiting to be loaded.
                waitingToLoad.add(0, intf);

                //we will set the recursive indicator to this class so that if we try to load it again and fail, we know we have a circular dependency.
                recursiveIndicator = intf;
            }
        }
        return readyToLoad;
    }

    private static boolean isNullable(Parameter param) {
        return Arrays.stream(param.getAnnotations()).anyMatch(a -> a.annotationType().getSimpleName().equalsIgnoreCase("nullable"));
    }

    /**
     * Checks if all the parameters can be loaded from the given set of classes.
     * @param params The parameters to check.
     * @param loadedBefore The classes that will have already been loaded.
     * @return True if all the parameters can be loaded from the given set of classes.
     */
    private boolean canBeLoaded(Parameter[] params, Set<Class<?>> loadedBefore) {
        var temp = new HashSet<>(loadedBefore);
        temp.add(IServiceProvider.class);//service provider can always be provided.
        temp.add(Plugin.class);//plugin can always be provided.
        return Stream.of(params).allMatch(param -> temp.contains(param.getType())
                || (isNullable(param) && (!singletons.containsKey(param.getType()) && !transients.containsKey(param.getType()) && (parent == null || parent.getService(param.getType()) == null))));
    }

    /**
     * Generates a function that will instantiate a class with a constructor that has parameters derived from the service provider.
     * @param serviceImplementation The class to instantiate.
     * @param <S> The service interface
     * @param <I> The service implementation
     * @return A function that will instantiate the class.
     */
    private <S, I extends S> Function<IServiceProvider, S> generationFunction(Class<I> serviceImplementation) {
        var constructor = Arrays.stream(serviceImplementation.getConstructors()).max(Comparator.comparingInt(Constructor::getParameterCount)).orElseThrow(() -> new RuntimeException("[" + providerName + "ServiceProviderBuilder] Cannot instantiate " + serviceImplementation.getSimpleName() + " because it has no public constructors."));
        return (s) -> {
            var params = constructor.getParameters();
            var args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                var param = params[i];
                var paramType = param.getType();
                var paramAnnotations = param.getAnnotations();
                //if it has a @Nullable annotation, we will try to get the service, but if it is not found, we will pass null.
                if (Arrays.stream(paramAnnotations).anyMatch(a -> a.annotationType().getSimpleName().equalsIgnoreCase("nullable")))
                    args[i] = s.getService(paramType);
                else args[i] = s.getRequiredService(paramType);
            }
            try {
                return (S) constructor.newInstance(args);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("[" + providerName + "ServiceProviderBuilder] Failed to instantiate " + serviceImplementation.getSimpleName() + " because of an exception.", e);
            }
        };
    }


}
