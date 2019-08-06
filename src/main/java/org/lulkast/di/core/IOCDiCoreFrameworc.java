package org.lulkast.di.core;

import com.google.common.base.Strings;
import org.lulkast.di.annotations.Component;
import org.lulkast.di.annotations.Inject;
import org.lulkast.di.annotations.MyFrameworkBootStart;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class IOCDiCoreFrameworc {
    private static Map<Class<?>, Object> iocContainer = new HashMap();
    private static HashMap<String, BeanDefinition> beanDefinitionHashMap = new HashMap<>();

    private IOCDiCoreFrameworc() {
    }

    public static void init(String packageName) {
        try {
            Reflections reflections = new Reflections(packageName, new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());
            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Component.class);
            Set<Constructor> constructors = new HashSet<>();
            typesAnnotatedWith.stream()
                    .forEach(clazz -> Arrays.stream(clazz.getConstructors())
                            .forEach(constructor -> constructors.add(constructor)));

            for (Constructor constructor : constructors) {
                String name = constructor.getDeclaringClass().getSimpleName();
                Class clazz = constructor.getDeclaringClass();
                Class interf = clazz.getInterfaces()[0];
                Class[] dependencies = constructor.getParameterTypes();
                if (dependencies.length == 0) beanDefinitionHashMap.put(name, new BeanDefinition(clazz, interf, null));
                else for (Class dependency : dependencies) {
                    beanDefinitionHashMap.put(name, new BeanDefinition(clazz, interf, dependency));
                }
            }

            HashMap<String, BeanDefinition> beanDefinitionHashMapCopy = new HashMap<>();
            beanDefinitionHashMapCopy.putAll(beanDefinitionHashMap);

            while (!beanDefinitionHashMap.isEmpty()) {
                for (Map.Entry<String, BeanDefinition> stringBeanDefinitionEntry : beanDefinitionHashMapCopy.entrySet()) {
                    createBeans(stringBeanDefinitionEntry);
                }
            }
            createDataBaseIfNeeded();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Fuck");
        }
    }

    private static void createBeans (Map.Entry<String, BeanDefinition> map) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = map.getValue().clazz;
        Class dependency = map.getValue().dependency;
        Class inter = map.getValue().interf;
        Object bean = null;
        if (Objects.isNull(dependency)) {
            Constructor constructor = clazz.getConstructor(null);
            bean = constructor.newInstance();
        } else if (iocContainer.containsKey(dependency)) {
            Constructor constructor = clazz.getConstructor(dependency);
            bean = constructor.newInstance(iocContainer.get(dependency));
        }
        if (Objects.nonNull(bean)){
            iocContainer.put(inter, bean);
            beanDefinitionHashMap.remove(map.getKey());
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
