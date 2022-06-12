package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.exception.ArgumentParseException;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.flag.Flag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentParser {
    
    static void parseArguments(CommandContext context, boolean silent) throws ArgumentParseException {

        Map<Class<? extends Flag<?>>, String> classToFlag = new HashMap<>();
        Map<Flag<?>, String> flagToObj = new HashMap<>();

        List<String> args = new ArrayList<>(context.getArgs());
        for (Flag<?> flag : context.getCommand().getFlags()) {
            if (flag.hasSplitter()) {
                for (int i = 0; i < args.size(); i++) {
                    String arg = args.get(i);
                    if (arg.startsWith(flag.getIndicator() + flag.getSplitter())) {
                        //Check if the split contains the following argument directly after it or the next argument
                        if (arg.split(flag.getSplitter())[1].isEmpty()) {
                            if (args.size() <= i + 1) throw new ArgumentParseException(flag.getIndicator() + "Has no following value", silent);
                            else {
                                classToFlag.put((Class<? extends Flag<?>>) flag.getClass(), flag.getIndicator());
                                flagToObj.put(flag, args.get(i + 1));
                                args.remove(i + 1);
                                args.remove(i);
                            }
                        } else {
                            classToFlag.put((Class<? extends Flag<?>>) flag.getClass(), flag.getIndicator());
                            flagToObj.put(flag, arg.split(flag.getSplitter())[1]);
                            args.remove(i);
                        }

                        break;
                    }
                }

            } else {
                if (flag.hasArgument()) {
                    for (int i = 0; i < args.size(); i++) {
                        String arg = args.get(i);
                        if (arg.equalsIgnoreCase("-" + flag.getIndicator())) {
                            //Check if the split contains the following argument directly after it or the next argument
                            if (args.size() <= i + 1) throw new ArgumentParseException(flag.getIndicator() + "Has no following value", silent);
                            else {
                                classToFlag.put((Class<? extends Flag<?>>) flag.getClass(), flag.getIndicator());
                                flagToObj.put(flag, args.get(i + 1));
                                args.remove(i + 1);
                                args.remove(i);
                                break;
                            }
                        }
                    }
                }
                else {
                    for (int i = 0; i < args.size(); i++) {
                        String arg = args.get(i);
                        if (arg.equalsIgnoreCase("-" + flag.getIndicator())) {
                            classToFlag.put((Class<? extends Flag<?>>) flag.getClass(), flag.getIndicator());
                            flagToObj.put(flag, null);
                            args.remove(i);
                            break;
                        }
                    }
                }
            }
        }
        completeParse(classToFlag, flagToObj, args, context);

    }

    private static void completeParse(Map<Class<? extends Flag<?>>, String> classToFlag, Map<Flag<?>, String> flagAndArgument, List<String> args, CommandContext context) {
        Map<String, Object> flagToObj = new HashMap<>();
        flagAndArgument.forEach((flag, arg) -> {
            if (!flag.hasArgument()) flagToObj.put(flag.getIndicator(), true);
            else {
                try {
                    flagToObj.put(flag.getIndicator(), flag.parse(context, arg));
                } catch (PDKCommandException e) {
                    e.showError(context.getSender());
                }
            }
        });
        context.setArgs(args.toArray(new String[0]));
        context.setFlags(classToFlag, flagToObj);
    }

}
