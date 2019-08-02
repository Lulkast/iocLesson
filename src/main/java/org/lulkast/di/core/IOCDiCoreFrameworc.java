package org.lulkast.di.core;

import com.google.common.base.Strings;
import org.lulkast.di.annotations.Component;
import org.lulkast.di.annotations.Inject;
import org.lulkast.di.annotations.MyFrameworkBootStart;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class IOCDiCoreFrameworc {
    private static Map<Class<?>, Object> iocContainer = new HashMap();

    private IOCDiCoreFrameworc() {
    }

    public static void init(String packageName) {
        try {
            Reflections reflections = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());
            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Component.class);
            for (Class<?> aClass : typesAnnotatedWith) {
                Constructor<?> constructor = aClass.getConstructor(null);
                Object bean = constructor.newInstance();
                Class<?>[] interfaces = aClass.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    iocContainer.put(anInterface, bean);
                }
            }
            injectAnnotationBeanProcessor();
            createDataBaseIfNeeded();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Fuck");
        }
    }

    private static void createDataBaseIfNeeded() throws InvocationTargetException, IllegalAccessException {
        Collection<Object> beans = iocContainer.values();
        for (Object bean : beans) {
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (Objects.nonNull(declaredMethod.getDeclaredAnnotation(Inject.class))) {
                    declaredMethod.invoke(bean);
                }
            }
        }
    }

    private static void injectAnnotationBeanProcessor() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Map.Entry<Class<?>, Object> set : iocContainer.entrySet()) {
            Object bean = set.getValue();
            Constructor[] constructors = bean.getClass().getConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.isAnnotationPresent(Inject.class)) {
                    constructor.setAccessible(true);
                    Class<?> interfaceOfImplementedSomething = constructor.getParameterTypes()[0];
                    Object dependency = getByInterface(interfaceOfImplementedSomething);
                    bean = constructor.newInstance(dependency);
                    iocContainer.put(set.getKey(), bean);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getByInterface(Class<T> someInterface) {
        T bean = (T) iocContainer.get(someInterface);
        if (Objects.isNull(bean))
            throw new Error("not implementation found of " + someInterface.getName());
        return bean;
    }

    public static void run(Class<?> mainClass) {
        MyFrameworkBootStart annotation = mainClass.getAnnotation(MyFrameworkBootStart.class);
        if (Objects.isNull(annotation)) throw new RuntimeException("MyFrameworkBootStart not found");
        String pacagePass = annotation.value();
        if (Strings.isNullOrEmpty(pacagePass)) pacagePass = mainClass.getPackageName();
        init(pacagePass);
    }
}
