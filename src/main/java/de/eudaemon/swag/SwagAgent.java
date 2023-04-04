package de.eudaemon.swag;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

import java.lang.management.ManagementFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import java.io.IOException;

import java.awt.Component;
import java.awt.Container;

import java.awt.event.KeyEvent;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import javax.swing.KeyStroke;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

public class SwagAgent {
    public static final Map<Component, PlacementInfo> additionTraces = new HashMap<>();

    public static class ToStringAdvice {
        @Advice.OnMethodEnter
        public static void before(@Advice.AllArguments Object[] args, @Advice.This Container thiz) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            additionTraces.put(
                    (Component) args[0], new PlacementInfo(args[1], (Integer) args[2], stackTrace));
        }
    }

    public static void premain(String optionsString, Instrumentation inst)
            throws UnmodifiableClassException, MalformedObjectNameException,
                    NotCompliantMBeanException, InstanceAlreadyExistsException,
                    MBeanRegistrationException, IOException {
        Options options = Options.parse(optionsString);
        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(RETRANSFORMATION)
                .ignore(none())
                .type(is(Container.class))
                .transform(
                        (builder, typeDescription, classLoader, module) ->
                                builder.visit(Advice.to(ToStringAdvice.class).on(named("addImpl"))))
                .installOn(inst);
        inst.appendToBootstrapClassLoaderSearch(options.agentJar);
        inst.retransformClasses(Container.class);
        ComponentInfo componentInfoMBean =
                new ComponentInfo(KeyStroke.getKeyStroke(options.keyCode, options.modifiers));
        ObjectName objectName = new ObjectName("de.eudaemon.swag:type=ComponentInfo");
        ManagementFactory.getPlatformMBeanServer().registerMBean(componentInfoMBean, objectName);
    }

    private static class Options {
        final JarFile agentJar;
        final int keyCode;
        final int modifiers;

        private Options(JarFile agentJar_, int keyCode_, int modifiers_) {
            agentJar = agentJar_;
            keyCode = keyCode_;
            modifiers = modifiers_;
        }

        static Options parse(String optLine) throws IOException {
            String[] options = optLine.split(",");
            JarFile jarFile = null;
            int keyCode = KeyEvent.VK_F12;
            int modifiers = 0;

            for (String option : options) {
                int separator = option.indexOf(':');
                String key = option.substring(0, separator);
                String value = option.substring(separator + 1);
                if ("agentJar".equals(key)) {
                    jarFile = new JarFile(value);
                } else if ("keyCode".equals(key)) {
                    keyCode = Integer.parseInt(value);
                } else if ("modifiers".equals(key)) {
                    modifiers = Integer.parseInt(value);
                } else {
                    throw new IllegalArgumentException("Unknown option: " + key);
                }
            }
            if (jarFile == null) {
                throw new IllegalArgumentException("Missing jarFile option");
            }
            return new Options(jarFile, keyCode, modifiers);
        }
    }
}
