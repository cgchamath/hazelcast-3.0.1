/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.spring.context;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.executor.RunnableAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author mdogan 4/6/12
 */
public class SpringManagedContext implements ManagedContext, ApplicationContextAware {

    private AutowireCapableBeanFactory beanFactory;

    public SpringManagedContext() {
        super();
    }

    public Object initialize(Object obj) {
        if (obj != null) {
            if (obj instanceof RunnableAdapter) {
                RunnableAdapter adapter = (RunnableAdapter) obj;
                Object runnable = adapter.getRunnable();
                runnable = initializeIfSpringAwareIsPresent(runnable);
                adapter.setRunnable((Runnable) runnable);
            } else {
                obj = initializeIfSpringAwareIsPresent(obj);
            }
        }
        return obj;
    }

    private Object initializeIfSpringAwareIsPresent(Object obj) {
        Class clazz = obj.getClass();
        SpringAware s = (SpringAware) clazz.getAnnotation(SpringAware.class);
        if (s != null) {
            String name = s.beanName();
            if (name == null || name.length() == 0) {
                name = clazz.getName();
            }
            beanFactory.autowireBean(obj);
            obj = beanFactory.initializeBean(obj, name);
        }
        return obj;
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }
}
