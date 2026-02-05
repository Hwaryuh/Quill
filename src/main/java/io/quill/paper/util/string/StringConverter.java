package io.quill.paper.util.string;

public interface StringConverter<T> {
    T convert(String value) throws Exception;
}