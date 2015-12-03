package it.negro.contabilitapp.json;

import com.google.gson.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HierarchicalJsonDeserializer implements JsonDeserializer<Object> {

    private Properties properties;

    public HierarchicalJsonDeserializer() {
        init();
    }

    public HierarchicalJsonDeserializer(Properties properties) {
        init();
        this.properties.putAll(properties);
    }

    private void init() {
        this.properties = new Properties();
        this.properties.setProperty("date.pattern", "dd/MM/yyyy");
    }

    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Object result = null;
        try {
            if (json.isJsonObject()) {
                JsonObject jobj = json.getAsJsonObject();
                JsonElement remoteClass = jobj.get("remoteClass");
                if (remoteClass != null)
                    result = deserializeObject(jobj, Class.forName(remoteClass.getAsString()));
                else
                    result = deserializeObject(jobj, (Class<?>) typeOfT);
            } else if (json.isJsonArray()) {
                result = deserializeToArray(json.getAsJsonArray(), (Class<?>) typeOfT);
            } else if (json.isJsonNull()) {
                result = deserializeNull(json.getAsJsonNull(), (Class<?>) typeOfT);
            } else if (json.isJsonPrimitive()) {
                result = deserializePrimitive(json.getAsJsonPrimitive(), (Class<?>) typeOfT);
            }
            return result;
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    private Object deserializeObject(JsonObject emt, Class<?> remoteClass) throws Exception {
        Object result = remoteClass.newInstance();
        Method setter = null;
        Object attribute = null;
        JsonObject jobj = null;
        JsonElement remoteClassElement = null;
        for (Map.Entry<String, JsonElement> entry : emt.entrySet()) {
            setter = getSetter(remoteClass, entry.getKey());
            if (setter == null)
                continue;
            if (entry.getValue().isJsonObject()) {
                jobj = entry.getValue().getAsJsonObject();
                remoteClassElement = jobj.get("remoteClass");
                if (remoteClassElement != null) {
                    attribute = deserializeObject(jobj, Class.forName(remoteClassElement.getAsString()));
                } else {
                    Class<?> setterArgumentType = setter.getParameterTypes()[0];
                    if (Map.class.isAssignableFrom(setterArgumentType)) {
                        Type type = ((Type[]) setter.getGenericParameterTypes())[0];
                        Class<?> valueClass = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[1];
                        attribute = deserializeToMap(jobj, valueClass);
                    } else
                        attribute = deserializeObject(jobj, setter.getParameterTypes()[0]);
                }
            } else if (entry.getValue().isJsonPrimitive()) {
                attribute = deserializePrimitive(entry.getValue().getAsJsonPrimitive(), setter);
            } else if (entry.getValue().isJsonArray()) {
                attribute = deserializeArray(entry.getValue().getAsJsonArray(), remoteClass, setter);
            } else if (entry.getValue().isJsonNull()) {
                attribute = deserializeNull(entry.getValue().getAsJsonNull(), setter);
            }
            setter.invoke(result, attribute);
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<?, ?> deserializeToMap(JsonObject jobj, Class<?> componentClass) {
        Map map = new HashMap();
        for (Map.Entry<String, JsonElement> entry : jobj.entrySet())
            map.put(entry.getKey(), deserialize(entry.getValue(), componentClass, null));
        return map;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<?> deserializeToList(JsonArray jarray, Class<?> componentClass) {
        List list = new ArrayList();
        for (JsonElement emt : jarray) {
            list.add(deserialize(emt, componentClass, null));
        }
        return list;
    }

    public Object[] deserializeToArray(JsonArray jarray, Class<?> componentClass) {
        Object[] array = (Object[]) Array.newInstance(componentClass, jarray.size());
        Object element = null;
        for (int i = 0; i < jarray.size(); i++) {
            element = deserialize(jarray.get(i), componentClass, null);
            array[i] = element;
        }
        return array;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Set<?> deserializeToSet(JsonArray jarray, Class<?> componentClass) {
        Set set = new HashSet();
        for (JsonElement emt : jarray) {
            set.add(deserialize(emt, componentClass, null));
        }
        return set;
    }

    private Object deserializeNull(JsonNull jnull, Method setter) {
        Class<?> argType = setter.getParameterTypes()[0];
        return deserializeNull(jnull, argType);
    }

    public Object deserializeNull(JsonNull jnull, Class<?> desiredClass) {
        if (desiredClass.equals(Byte.TYPE))
            return 0;
        if (desiredClass.equals(Short.TYPE))
            return 0;
        if (desiredClass.equals(Integer.TYPE))
            return 0;
        if (desiredClass.equals(Long.TYPE))
            return 0l;
        if (desiredClass.equals(Float.TYPE))
            return 0.0f;
        if (desiredClass.equals(Double.TYPE))
            return 0.0d;
        if (desiredClass.equals(Boolean.TYPE))
            return false;
        return null;
    }

    private Object deserializePrimitive(JsonPrimitive primitive, Method setter) {
        Class<?> argType = setter.getParameterTypes()[0];
        return deserializePrimitive(primitive, argType);
    }

    public Object deserializePrimitive(JsonPrimitive primitive, Class<?> desiredClass) {
        if (desiredClass.equals(Byte.TYPE) || desiredClass.equals(Byte.class))
            return primitive.getAsByte();
        if (desiredClass.equals(Short.TYPE) || desiredClass.equals(Short.class))
            return primitive.getAsShort();
        if (desiredClass.equals(Integer.TYPE) || desiredClass.equals(Integer.class))
            return primitive.getAsInt();
        if (desiredClass.equals(Long.TYPE) || desiredClass.equals(Long.class))
            return primitive.getAsLong();
        if (desiredClass.equals(Float.TYPE) || desiredClass.equals(Float.class))
            return primitive.getAsFloat();
        if (desiredClass.equals(Double.TYPE) || desiredClass.equals(Double.class))
            return primitive.getAsDouble();
        if (desiredClass.equals(Boolean.TYPE) || desiredClass.equals(Boolean.class))
            return primitive.getAsBoolean();
        if (desiredClass.equals(BigInteger.class))
            return primitive.getAsBigInteger();
        if (desiredClass.equals(BigDecimal.class))
            return primitive.getAsBigDecimal();
        if (desiredClass.equals(String.class))
            return primitive.getAsString();
        if (desiredClass.equals(Character.TYPE) || desiredClass.equals(Character.class))
            return primitive.getAsCharacter();
        if (desiredClass.equals(Date.class)) {
            try {
                return new SimpleDateFormat(this.properties.getProperty("date.pattern")).parse(primitive.getAsString());
            } catch (ParseException e) {
                return null;
            }
        }
        if (desiredClass.isEnum()) {
            try {
                Method method = desiredClass.getMethod("valueOf", String.class);
                return method.invoke(null, primitive.getAsString());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private Object deserializeArray(JsonArray jarray, Class<?> ownerClass, Method setter) {
        Object result = null;
        Class<?> elementType = null;
        Class<?> attributeType = setter.getParameterTypes()[0];

        if (attributeType.isArray())
            elementType = attributeType.getComponentType();
        else
            elementType = (Class<?>) ((ParameterizedType) setter.getGenericParameterTypes()[0]).getActualTypeArguments()[0];

        if (attributeType.isArray())
            result = deserializeToArray(jarray, elementType);
        else if (List.class.isAssignableFrom(attributeType))
            result = deserializeToList(jarray, elementType);
        else if (Set.class.isAssignableFrom(attributeType))
            result = deserializeToSet(jarray, elementType);
        return result;
    }

    private Method getSetter(Class<?> clazz, String name) {
        Method[] methods = clazz.getMethods();
        String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        for (Method method : methods) {
            if (method.getName().equals(setterName))
                return method;
        }
        return null;
    }
}