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

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

public class SwagAgent {
    public static final Map<Component, StackTraceElement[]> additionTraces = new HashMap<>();

    public static class ToStringAdvice {
        @Advice.OnMethodEnter
        public static void before(@Advice.AllArguments Object[] args, @Advice.This Container thiz) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            System.out.println("ToStringAdvice.before");
            System.out.println("Adding trace:");
            System.out.println(stackTrace[0]);
            additionTraces.put((Component) args[0], stackTrace);
        }
    }

    public static void premain(String options, Instrumentation inst)
            throws UnmodifiableClassException, MalformedObjectNameException,
                    NotCompliantMBeanException, InstanceAlreadyExistsException,
                    MBeanRegistrationException, IOException {
        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(RETRANSFORMATION)
                .ignore(none())
                .type(is(Container.class))
                .transform(
                        (builder, typeDescription, classLoader, module) ->
                                builder.visit(Advice.to(ToStringAdvice.class).on(named("addImpl"))))
                .installOn(inst);
        inst.appendToBootstrapClassLoaderSearch(new JarFile(options));
        inst.retransformClasses(Container.class);
        ComponentInfo componentInfoMBean = new ComponentInfo();
        ObjectName objectName = new ObjectName("de.eudaemon.swag:type=ComponentInfo");
        ManagementFactory.getPlatformMBeanServer().registerMBean(componentInfoMBean, objectName);
    }
}
