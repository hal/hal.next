/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.dmr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.base.CharMatcher;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jboss.hal.js.Json;
import org.jboss.hal.js.JsonObject;
import org.jboss.hal.spi.EsReturn;

import static org.jboss.hal.dmr.ModelDescriptionConstants.FAILURE_DESCRIPTION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.OUTCOME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SUCCESS;

/**
 * A dynamic model representation node object.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@JsType
public class ModelNode implements Cloneable {

    /**
     * Creates a new node from a base64 encoded string
     *
     * @param encoded The base64 encoded string.
     *
     * @return the new model node
     */
    public static ModelNode fromBase64(String encoded) {
        // Bloody IE can't cope with line breaks when decoding base64!
        String safeEncoded = CharMatcher.breakingWhitespace().removeFrom(encoded);
        ModelNode node = new ModelNode();
        String decoded = Base64.decode(safeEncoded);
        node.readExternal(new DataInput(toBytes(decoded)));
        return node;
    }

    private static native byte[] toBytes(String str) /*-{
        var bytes = [];
        for (var i = 0; i < str.length; ++i) {
            bytes.push(str.charCodeAt(i));
        }
        return bytes;
    }-*/;

    private static final String NEW_VALUE_IS_NULL = "newValue is null";

    private boolean protect = false;
    private ModelValue value;

    @JsIgnore
    public ModelNode() {
        this.value = ModelValue.UNDEFINED;
    }

    ModelNode(ModelValue value) {
        this.value = value;
    }

    /**
     * Prevent further modifications to this node and its sub-nodes. Note that copies
     * of this node made after this method call will not be protected.
     */
    @JsIgnore
    public void protect() {
        if (!protect) {
            protect = true;
            value = value.protect();
        }
    }

    /**
     * Get the value of this node as a {@code long}. Collection types will return the size
     * of the collection for this value. Other types may attempt a string conversion.
     *
     * @return the long value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    @JsIgnore
    public long asLong() throws IllegalArgumentException {
        return value.asLong();
    }

    /**
     * Get the value of this node as a {@code long}. Collection types will return the size
     * of the collection for this value. Other types may attempt a string conversion.
     *
     * @param defVal the default value if no conversion is possible
     *
     * @return the long value
     */
    @JsIgnore
    public long asLong(long defVal) {
        return value.asLong(defVal);
    }

    /**
     * Get the value of this node as number. Collection types will return the size
     * of the collection for this value. Other types may attempt a string conversion.
     *
     * @return the numeric value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    public int asInt() throws IllegalArgumentException {
        return value.asInt();
    }

    /**
     * Get the value of this node as number. Collection types will return the size
     * of the collection for this value. Other types may attempt a string conversion.
     *
     * @param defVal the default value if no conversion is possible
     *
     * @return the int value
     */
    @JsIgnore
    public int asInt(int defVal) {
        return value.asInt(defVal);
    }

    /**
     * Get the value of this node as a boolean. Collection types return true for non-empty collections. Numerical types
     * return true for non-zero values.
     *
     * @return the boolean value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    public boolean asBoolean() throws IllegalArgumentException {
        return value.asBoolean();
    }

    /**
     * Get the value of this node as a {@code boolean}. Collection types return {@code true} for non-empty
     * collections. Numerical types return {@code true} for non-zero values.
     *
     * @param defVal the default value if no conversion is possible
     *
     * @return the boolean value
     */
    @JsIgnore
    public boolean asBoolean(boolean defVal) {
        return value.asBoolean(defVal);
    }

    /**
     * Get the value as a string. This is the literal value of this model node. More than one node type may
     * yield the same value for this method.
     *
     * @return the string value
     */
    public String asString() {
        return value.asString();
    }

    /**
     * Get the value of this node as a {@code double}. Collection types will return the size
     * of the collection for this value. Other types may attempt a string conversion.
     *
     * @return the double value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    @JsIgnore
    public double asDouble() throws IllegalArgumentException {
        return value.asDouble();
    }

    /**
     * Get the value of this node as an {@code double}. Collection types will return the size
     * of the collection for this value. Other types may attempt a string conversion.
     *
     * @param defVal the default value if no conversion is possible
     *
     * @return the int value
     */
    @JsIgnore
    public double asDouble(double defVal) {
        return value.asDouble(defVal);
    }

    /**
     * Get the value of this node as a type, expressed using the {@code ModelType} enum. The string
     * value of this node must be convertible to a type.
     *
     * @return the {@code ModelType} value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    @JsIgnore
    public ModelType asType() throws IllegalArgumentException {
        return value.asType();
    }

    /**
     * Get the value of this node as a {@code BigDecimal}. Collection types will return the size
     * of the collection for this value. Other types may attempt a string conversion.
     *
     * @return the {@code BigDecimal} value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    @JsIgnore
    public BigDecimal asBigDecimal() throws IllegalArgumentException {
        return value.asBigDecimal();
    }

    /**
     * Get the value of this node as a {@code BigInteger}. Collection types will return the size
     * of the collection for this value. Other types may attempt a string conversion.
     *
     * @return the {@code BigInteger} value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    @JsIgnore
    public BigInteger asBigInteger() throws IllegalArgumentException {
        return value.asBigInteger();
    }

    /**
     * Get the value of this node as a byte array. Strings and string-like values will return
     * the UTF-8 encoding of the string. Numerical values will return the byte representation of the
     * number.
     *
     * @return the bytes
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    @JsIgnore
    public byte[] asBytes() throws IllegalArgumentException {
        return value.asBytes();
    }

    /**
     * Get the value of this node as a property. Object values will return a property if there is exactly one
     * property in the object. List values will return a property if there are exactly two items in the list,
     * and if the first is convertible to a string.
     *
     * @return the property value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    public Property asProperty() throws IllegalArgumentException {
        return value.asProperty();
    }

    /**
     * Get the value of this node as a property list. Object values will return a list of properties representing
     * each key-value pair in the object. List values will return all the values of the list, failing if any of the
     * values are not convertible to a property value.
     *
     * @return the property list value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    @JsIgnore
    public List<Property> asPropertyList() throws IllegalArgumentException {
        return value.asPropertyList();
    }

    /**
     * Get a copy of this value as an object. Object values will simply copy themselves.
     * <p>
     * Property values will return a single-entry object whose key and value are copied from the property key and value.
     * <p>
     * List values will attempt to interpolate the list into an object by iterating each item, mapping each property
     * into an object entry and otherwise taking pairs of list entries, converting the first to a string, and using the
     * pair of entries as a single object entry. If an object key appears more than once in the source object, the
     * last key takes precedence.
     *
     * @return the object value
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    public ModelNode asObject() throws IllegalArgumentException {
        return value.asObject();
    }

    /**
     * Get the value of this node as {@code type}. This method simply delegates to the various {@code asXXX()}
     * methods depending on the specified type.
     *
     * @return the value as {@code type} or null if the type is {@link ModelType#UNDEFINED}
     *
     * @throws IllegalArgumentException if no conversion is possible
     */
    @JsIgnore
    public Object as(ModelType type) {
        Object result;
        switch (type) {
            case BIG_DECIMAL:
                result = asBigDecimal();
                break;
            case BIG_INTEGER:
                result = asBigInteger();
                break;
            case BOOLEAN:
                result = asBoolean();
                break;
            case BYTES:
                result = asBytes();
                break;
            case DOUBLE:
                result = asDouble();
                break;
            case EXPRESSION:
                result = asString();
                break;
            case INT:
                result = asInt();
                break;
            case LIST:
                result = asList();
                break;
            case LONG:
                result = asLong();
                break;
            case OBJECT:
                result = asObject();
                break;
            case PROPERTY:
                result = asProperty();
                break;
            case STRING:
                result = asString();
                break;
            case TYPE:
                result = asType();
                break;
            case UNDEFINED:
                result = null;
                break;
            default:
                result = null;
                break;
        }
        return result;
    }

    /**
     * Determine whether this node is defined.
     *
     * @return true if this node's value is defined, false otherwise
     */
    @JsProperty
    public boolean isDefined() {
        return getType() != ModelType.UNDEFINED;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsMethod(name = "setNumber")
    public ModelNode set(int newValue) {
        checkProtect();
        value = new IntModelValue(newValue);
        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(long newValue) {
        checkProtect();
        value = new LongModelValue(newValue);
        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(double newValue) {
        checkProtect();
        value = new DoubleModelValue(newValue);
        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsMethod(name = "setBoolean")
    public ModelNode set(boolean newValue) {
        checkProtect();
        value = BooleanModelValue.valueOf(newValue);
        return this;
    }

    /**
     * Change this node's value to the given expression value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    public ModelNode setExpression(String newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        checkProtect();
        value = new ExpressionValue(newValue);
        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsMethod(name = "setString")
    public ModelNode set(String newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        checkProtect();
        value = new StringModelValue(newValue);
        return this;
    }

    @JsIgnore
    public ModelNode set(ModelType type, Object propValue) {
        if (type.equals(ModelType.STRING)) {
            set((String) propValue);
        } else if (type.equals(ModelType.INT)) {
            set((Integer) propValue);
        } else if (type.equals(ModelType.DOUBLE)) {
            set((Double) propValue);
        } else if (type.equals(ModelType.LONG)) {
            set((Long) propValue);
        } else if (type.equals(ModelType.BOOLEAN)) {
            set((Boolean) propValue);
        } else if (type.equals(ModelType.LIST)) {
            setEmptyList();
            List list = (List) propValue;

            for (Object item : list) {
                add(String.valueOf(item));
            }
        } else {
            throw new RuntimeException("Type conversion not implemented for " + type);
        }

        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(BigDecimal newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        checkProtect();
        value = new BigDecimalModelValue(newValue);
        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(BigInteger newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        checkProtect();
        value = new BigIntegerModelValue(newValue);
        return this;
    }

    /**
     * Change this node's value to the given value. The value is copied from the parameter.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsMethod(name = "setNode")
    public ModelNode set(ModelNode newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        checkProtect();
        value = newValue.value.copy();
        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(byte[] newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        checkProtect();
        byte[] clone = new byte[newValue.length];
        System.arraycopy(newValue, 0, clone, 0, newValue.length);

        value = new BytesModelValue(newValue.length == 0 ? newValue : clone);
        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(ModelType newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        checkProtect();
        value = TypeModelValue.of(newValue);
        return this;
    }

    /**
     * Change this node's value to the given value.
     *
     * @param newValue the new value
     *
     * @return this node
     */
    @JsMethod(name = "setProperty")
    public ModelNode set(Property newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        set(newValue.getName(), newValue.getValue());
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, ModelNode propertyValue) {
        checkProtect();
        value = new PropertyModelValue(propertyName, propertyValue);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, int propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, long propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, double propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, boolean propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, String propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and expression value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property expression value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode setExpression(String propertyName, String propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.setExpression(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, BigDecimal propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, BigInteger propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, byte[] propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a property with the given name and value.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(String propertyName, ModelType propertyValue) {
        checkProtect();
        ModelNode node = new ModelNode();
        node.set(propertyValue);
        value = new PropertyModelValue(propertyName, node);
        return this;
    }

    /**
     * Change this node's value to a list whose values are copied from the given collection.
     *
     * @param newValue the list value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode set(Collection<ModelNode> newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(NEW_VALUE_IS_NULL);
        }
        checkProtect();
        ArrayList<ModelNode> list = new ArrayList<>(newValue.size());
        for (ModelNode node : newValue) {
            if (node == null) {
                list.add(new ModelNode());
            } else {
                list.add(node.clone());
            }
        }
        value = new ListModelValue(list);
        return this;
    }

    /**
     * Change this node's value to an empty list.
     *
     * @return this node
     */
    public ModelNode setEmptyList() {
        checkProtect();
        value = new ListModelValue();
        return this;
    }

    /**
     * Change this node's value to an empty object.
     *
     * @return this node
     */
    public ModelNode setEmptyObject() {
        checkProtect();
        value = new ObjectModelValue();
        return this;
    }

    /**
     * Clear this node's value and change its type to undefined.
     *
     * @return this node
     */
    public ModelNode clear() {
        checkProtect();
        value = ModelValue.UNDEFINED;
        return this;
    }

    /**
     * Get the child of this node with the given name. If no such child exists, create it. If the node is undefined,
     * it will be initialized to be of type object.
     * <p>
     * When called on property values, the name must match the property name.
     *
     * @param name the child name
     *
     * @return the child
     *
     * @throws IllegalArgumentException if this node does not support getting a child with the given name
     */
    public ModelNode get(String name) {
        ModelValue value = this.value;
        if (value == ModelValue.UNDEFINED) {
            checkProtect();
            this.value = new ObjectModelValue();
            return this.value.getChild(name);
        }
        return value.getChild(name);
    }

    /**
     * Require the existence of a child of this node with the given name, returning the child. If no such child
     * exists, an exception is thrown.
     * <p>
     * When called on property values, the name must match the property name.
     *
     * @param name the child name
     *
     * @return the child
     *
     * @throws NoSuchElementException if the element does not exist
     */
    @JsIgnore
    public ModelNode require(String name) throws NoSuchElementException {
        return value.requireChild(name);
    }

    /**
     * Remove a child of this node, returning the child. If no such child exists, an exception is thrown.
     * <p>
     * When called on property values, the name must match the property name.
     *
     * @param name the child name
     *
     * @return the child
     *
     * @throws NoSuchElementException if the element does not exist
     */
    @JsIgnore
    public ModelNode remove(String name) throws NoSuchElementException {
        return value.removeChild(name);
    }

    /**
     * Get the child of this node with the given index. If no such child exists, create it (adding list entries as
     * needed). If the node is undefined, it will be initialized to be of type {@link ModelType#LIST}.
     * <p>
     * When called on property values, the index must be zero.
     *
     * @param index the child index
     *
     * @return the child
     *
     * @throws IllegalArgumentException if this node does not support getting a child with the given index
     */
    @JsIgnore
    public ModelNode get(int index) {
        ModelValue value = this.value;
        if (value == ModelValue.UNDEFINED) {
            checkProtect();
            return (this.value = new ListModelValue()).getChild(index);
        }
        return value.getChild(index);
    }

    /**
     * Require the existence of a child of this node with the given index, returning the child. If no such child
     * exists, an exception is thrown.
     * <p>
     * When called on property values, the index must be zero.
     *
     * @param index the child index
     *
     * @return the child
     *
     * @throws NoSuchElementException if the element does not exist
     */
    @JsIgnore
    public ModelNode require(int index) {
        return value.requireChild(index);
    }

    /**
     * Add the given value to the end of this node's value list. If the node is undefined, it will be initialized to
     * be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(int newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add the given value to the end of this node's value list. If the node is undefined, it will be initialized to
     * be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(long newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add the given value to the end of this node's value list. If the node is undefined, it will be initialized to
     * be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(double newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add the given value to the end of this node's value list. If the node is undefined, it will be initialized to
     * be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(boolean newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add the given expression to the end of this node's value list. If the node is undefined, it will be initialized
     * to be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode addExpression(String newValue) {
        add().setExpression(newValue);
        return this;
    }

    /**
     * Add the given value to the end of this node's value list. If the node is undefined, it will be initialized to
     * be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add the given value to the end of this node's value list. If the node is undefined, it will be initialized to
     * be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(BigDecimal newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add the given value to the end of this node's value list. If the node is undefined, it will be initialized to
     * be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(BigInteger newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add a copy of the given value to the end of this node's value list. If the node is undefined, it will be
     * initialized to be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(ModelNode newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add the given value to the end of this node's value list. If the node is undefined, it will be initialized to
     * be of type {@link ModelType#LIST}.
     *
     * @param newValue the new value to add
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(byte[] newValue) {
        add().set(newValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, int propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, long propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, double propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, boolean propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, String propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, BigDecimal propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, BigInteger propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, ModelNode propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a property with the given name and value to the end of this node's value list. If the node is undefined, it
     * will be initialized to be of type {@link ModelType#LIST}.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     *
     * @return this node
     */
    @JsIgnore
    public ModelNode add(String propertyName, byte[] propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }

    /**
     * Add a node to the end of this node's value list and return it. If the node is undefined, it will be initialized
     * to be of type list.
     *
     * @return the new node
     */
    public ModelNode add() {
        checkProtect();
        ModelValue value = this.value;
        if (value == ModelValue.UNDEFINED) {
            this.value = new ListModelValue();
            return this.value.addChild();
        }
        return value.addChild();
    }

    /**
     * Add a node of type {@link ModelType#LIST} to the end of this node's value list and return it. If this node is
     * undefined, it will be initialized to be of type {@link ModelType#LIST}.
     *
     * @return the new node
     */
    @JsIgnore
    public ModelNode addEmptyList() {
        ModelNode node = add();
        node.setEmptyList();
        return node;
    }

    /**
     * Add a node of type {@link ModelType#OBJECT} to the end of this node's value list and return it. If this node is
     * undefined, it will be initialized to be of type {@link ModelType#LIST}.
     *
     * @return the new node
     */
    @JsIgnore
    public ModelNode addEmptyObject() {
        ModelNode node = add();
        node.setEmptyObject();
        return node;
    }

    /**
     * Determine whether this node has a child with the given index. Property node types always contain exactly one
     * value.
     *
     * @param index the index
     *
     * @return {@code true} if there is a (possibly undefined) node at the given index
     */
    @JsIgnore
    public boolean has(int index) {
        return value.has(index);
    }

    /**
     * Determine whether this node has a child with the given name. Property node types always contain exactly one
     * value with a key equal to the property name.
     *
     * @param key the name
     *
     * @return true if there is a (possibly undefined) node at the given key
     */
    public boolean has(String key) {
        return value.has(key);
    }

    /**
     * Determine whether this node has a defined child with the given index. Property node types always contain
     * exactly one value.
     *
     * @param index the index
     *
     * @return {@code true} if there is a node at the given index and its {@link #getType() type} is not {@link
     * ModelType#UNDEFINED}
     */
    @JsIgnore
    public boolean hasDefined(int index) {
        return value.has(index) && get(index).isDefined();
    }

    /**
     * Determine whether this node has a defined child with the given name. Property node types always contain exactly
     * one value with a key equal to the property name.
     *
     * @param key the name
     *
     * @return true if there is a node at the given index and its type is not undefined
     */
    public boolean hasDefined(String key) {
        return value.has(key) && get(key).isDefined();
    }

    /**
     * Get the set of keys contained in this object. Property node types always contain exactly one value with a key
     * equal to the property name. Other non-object types will return an empty set.
     *
     * @return the key set
     */
    @JsIgnore
    public Set<String> keys() {
        return value.getKeys();
    }

    /**
     * Get the list of entries contained in this object. Property node types always contain exactly one entry
     * (itself). Lists will return an unmodifiable view of their contained list.  Objects will return a list of
     * properties corresponding to the mappings within the object. Other types will return an empty list.
     *
     * @return the entry list
     */
    @JsIgnore
    public List<ModelNode> asList() {
        return value.asList();
    }

    /**
     * Recursively get the children of this node with the given names. If any child along the path does not exist,
     * create it. If any node is the path is undefined, it will be initialized to be of type {@link ModelType#OBJECT}.
     *
     * @param names the child names
     *
     * @return the child
     *
     * @throws IllegalArgumentException if a node does not support getting a child with the given name path
     */
    @JsIgnore
    public ModelNode get(String... names) {
        ModelNode current = this;
        for (String part : names) {
            current = current.get(part);
        }
        return current;
    }

    /**
     * Get a human-readable string representation of this model node, formatted nicely (possibly on multiple lines).
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Get a JSON string representation of this model node, formatted nicely, if requested.
     *
     * @param compact Flag that indicates whether or not the string should be all on one line (i.e. {@code true}) or
     *                should be printed on multiple lines ({@code false}).
     *
     * @return The JSON string.
     */
    @JsIgnore
    public String toJSONString(boolean compact) {
        return value.toJSONString(compact);
    }

    @JsIgnore
    public String toJSONString() {
        return value.toJSONString(false);
    }

    public String toBase64String() {
        DataOutput out = new DataOutput();
        writeExternal(out);
        return Base64.encode(out.toString());
    }

    /**
     * Return a copy of this model node, with all system property expressions locally resolved. The caller must have
     * permission to access all of the system properties named in the node tree.
     *
     * @return the resolved copy
     */
    @JsIgnore
    public ModelNode resolve() {
        ModelNode newNode = new ModelNode();
        newNode.value = value.resolve();
        return newNode;
    }

    /**
     * Determine whether this object is equal to another.
     *
     * @param other the other object
     *
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    @Override
    @JsIgnore
    public boolean equals(Object other) {
        return other instanceof ModelNode && equals((ModelNode) other);
    }

    /**
     * Determine whether this object is equal to another.
     *
     * @param other the other object
     *
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    @JsIgnore
    public boolean equals(ModelNode other) {
        return this == other || other != null && other.value.equals(value);
    }

    /**
     * Get the hash code of this node object.  Note that unless the value is {@link #protect()}ed, the hash code may
     * change over time, thus making unprotected nodes unsuitable for use as hash table keys.
     *
     * @return the hash code
     */
    @JsIgnore
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Clone this model node.
     *
     * @return the clone
     */
    @JsIgnore
    public ModelNode clone() {
        ModelNode clone = new ModelNode();
        clone.value = value.copy();
        return clone;
    }

    protected void format(StringBuilder builder, int indent, boolean multiLine) {
        value.format(builder, indent, multiLine);
    }

    void formatAsJSON(StringBuilder builder, int indent, boolean multiLine) {
        value.formatAsJSON(builder, indent, multiLine);
    }

    /**
     * Get the current type of this node.
     *
     * @return the node type
     */
    @JsIgnore
    public ModelType getType() {
        return value.getType();
    }

    /**
     * Write this node's content in binary format to the given target.
     *
     * @param out the target to which the content should be written
     */
    @JsIgnore
    public void writeExternal(DataOutput out) {
        ModelValue value = this.value;
        ModelType type = value.getType();
        out.writeByte(type.getTypeChar());
        value.writeExternal(out);
    }

    /**
     * Read this node's content in binary format from the given source.
     *
     * @param in the source from which the content should be read
     */
    void readExternal(DataInput in) {
        checkProtect();
        byte[] b; // used by some of these
        try {
            ModelType type = ModelType.forChar((char) (in.readByte() & 0xff));
            switch (type) {
                case UNDEFINED:
                    value = ModelValue.UNDEFINED;
                    return;
                case BIG_DECIMAL:
                    value = new BigDecimalModelValue(in);
                    return;
                case BIG_INTEGER:
                    b = new byte[in.readInt()];
                    in.readFully(b);
                    value = new BigIntegerModelValue(new BigInteger(b));
                    return;
                case BOOLEAN:
                    value = BooleanModelValue.valueOf(in.readBoolean());
                    return;
                case BYTES:
                    b = new byte[in.readInt()];
                    in.readFully(b);
                    value = new BytesModelValue(b);
                    return;
                case DOUBLE:
                    value = new DoubleModelValue(in.readDouble());
                    return;
                case EXPRESSION:
                    value = new ExpressionValue(in.readUTF());
                    return;
                case INT:
                    value = new IntModelValue(in.readInt());
                    return;
                case LIST:
                    value = new ListModelValue(in);
                    return;
                case LONG:
                    value = new LongModelValue(in.readLong());
                    return;
                case OBJECT:
                    value = new ObjectModelValue(in);
                    return;
                case PROPERTY:
                    value = new PropertyModelValue(in);
                    return;
                case STRING:
                    value = new StringModelValue(in.readUTF());
                    return;
                case TYPE:
                    value = TypeModelValue.of(ModelType.forChar((char) (in.readByte() & 0xff)));
                    return;
                default:
                    throw new IllegalStateException("Invalid type read: " + type);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    private void checkProtect() {
        if (protect) {
            throw new UnsupportedOperationException();
        }
    }

    /** @return {@code true} if this node has an outcome and the outcome does not equal "success" */
    @JsProperty
    public boolean isFailure() {
        return hasDefined(OUTCOME) && !get(OUTCOME).asString().equals(SUCCESS);
    }

    /** @return the failure description or "No failure-description provided" */
    @JsProperty
    public String getFailureDescription() {
        if (hasDefined(FAILURE_DESCRIPTION)) {
            StringBuilder failure = new StringBuilder();
            get(FAILURE_DESCRIPTION).format(failure, 0, true);
            return failure.toString();
        } else {
            return "No failure-description provided";
        }
    }


    // ------------------------------------------------------ JS methods

    /**
     * Creates a new undefined model node
     *
     * @return the new model node
     */
    @JsMethod(name = "create")
    public static ModelNode jsCreate() {
        return new ModelNode();
    }

    /**
     * @return the model node as JSON
     */
    @JsMethod(name = "getJSON")
    @EsReturn("json")
    public JsonObject jsGetJSONString() {
        return Json.parse(toJSONString(true));
    }

    /**
     * @return this model node as an {@link Property} array
     */
    @JsMethod(name = "asProperties")
    @EsReturn("Property[]")
    public Property[] jsAsProperties() {
        List<Property> properties = value.asPropertyList();
        if (properties != null) {
            return properties.toArray(new Property[properties.size()]);
        }
        return new Property[0];
    }

    /**
     * @return this model node as an array.
     */
    @JsMethod(name = "asList")
    @EsReturn("ModelNode[]")
    public ModelNode[] jsAsList() {
        List<ModelNode> modelNodes = value.asList();
        if (modelNodes != null) {
            return modelNodes.toArray(new ModelNode[modelNodes.size()]);
        }
        return new ModelNode[0];
    }
}
