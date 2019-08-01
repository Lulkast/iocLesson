package org.lulkast.di.core;

import com.google.common.base.Strings;
import org.lulkast.di.annotations.MyFrameworkBootStart;
import org.reflections.Reflections;
import org.lulkast.di.annotations.Component;
import org.lulkast.di.annotations.Inject;
import ru.lulkast.Main;

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
            Reflections reflections = new Reflections(packageName);
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

    private static void injectAnnotationBeanProcessor() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Collection<Object> beans = iocContainer.values();
        for (Object bean : beans){
            Constructor constructor = bean.getClass().getConstructor(Inject.class);
            constructor.setAccessible(true);
            Class<?> interfaceOfImplementedSomething = constructor.getParameterTypes()[0];
            Object dependency = getByInterface(interfaceOfImplementedSomething);
            constructor.newInstance(dependency);
        }




       /* for (Object bean : beans) {
            Field[] declaredFields = bean.getClass().getDeclaredFields();//упростить с помощью библиотеки reflections
            for (Field declaredField : declaredFields) {
                if (Objects.nonNull(declaredField.getAnnotation(Inject.class))) {
                    declaredField.setAccessible(true);
                    Class<?> interfaceOfImplementedSomething = declaredField.getType();
                    Object dependency = getByInterface(interfaceOfImplementedSomething);
                    declaredField.set(bean, dependency);
                }
            }
        } */
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
