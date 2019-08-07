package org.lulkast.di.core;

public class BeanDefinition {
    Class clazz;
    Class interf;
    Class dependency;
    public BeanDefinition (Class clazz, Class interf, Class dependency){
        this.clazz = clazz;
        this.dependency = dependency;
        this.interf = interf;
    }
}
