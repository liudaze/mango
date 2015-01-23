/*
 * Copyright 2014 mango.jfaster.org
 *
 * The Mango Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.jfaster.mango.reflect;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * @author ash
 */
public class TypeWrapper {

    /**
     * type映射的原始类型
     * <p>
     * 如果type为{@code Integer}，它的值为{@code Integer.class}，
     * 如果type为{@code List<Integer>}，它的值为{@code Integer.class}，
     * 如果type为{@code List<List<String>>}，{@code List<T>>}等，它的值为{@code null}
     */
    private Class<?> mappedClass;

    /**
     * type映射的原始类型，抛出异常时需要用到
     */
    private Type mappedType;

    private boolean isList;
    private boolean isSet;
    private boolean isArray;

    public TypeWrapper(Type type) {
        if (byte[].class.equals(type)) { // byte[]是jdbc中的一个基础类型,所以不把它作为数组处理
            mappedClass = byte[].class;
        } else {
            TypeToken token = TypeToken.of(type);
            if (token.isArray()) { // 数组，也能处理泛型数组
                isArray = true;
                TypeToken componentToken = token.getComponentType();
                if (componentToken == null) {
                    throw new IllegalStateException();
                }
                mappedType = componentToken.getType();
                mappedClass = componentToken.getRawType();
            } else {
                Class<?> rawType = token.getRawType();
                if (List.class.equals(rawType)) { // 列表
                    isList = true;
                    TypeToken<?> mappedToken = token.resolveType(List.class.getTypeParameters()[0]);
                    mappedType = mappedToken.getType();
                    mappedClass = mappedToken.getRawType();
                } else if (Set.class.equals(rawType)) { // 集合
                    isSet = true;
                    TypeToken<?> mappedToken = token.resolveType(Set.class.getTypeParameters()[0]);
                    mappedType = mappedToken.getType();
                    mappedClass = mappedToken.getRawType();
                } else { // 普通类
                    mappedType = type;
                    mappedClass = rawType;
                }
            }
        }
    }

    public boolean isIterable() {
        return isList || isSet || isArray;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean isSet() {
        return isSet;
    }

    public boolean isList() {
        return isList;
    }

    public boolean isCollection() {
        return isList || isSet;
    }

    public Class<?> getMappedClass() {
        return mappedClass;
    }

    public Type getMappedType() {
        return mappedType;
    }

}










