package com.edurmus.librarymanagement.service.specification;

import com.edurmus.librarymanagement.model.annotation.SearchableField;
import com.edurmus.librarymanagement.model.enums.ComparisonOperation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GenericSpecification<D, T> {

    public Specification<T> build(D example) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Field field : example.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(SearchableField.class)) continue;

                Object value = getFieldValue(field, example);
                if (isNullOrEmpty(value)) continue;

                SearchableField annotation = field.getAnnotation(SearchableField.class);
                Predicate predicate = buildPredicate(root, builder, field.getName(), value, annotation.operation());
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Object getFieldValue(Field field, Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing field: " + field.getName(), e);
        }
    }

    private boolean isNullOrEmpty(Object value) {
        return value == null || (value instanceof String str && !StringUtils.hasText(str));
    }

    @SuppressWarnings("unchecked")
    private Predicate buildPredicate(Root<T> root, CriteriaBuilder builder, String fieldName, Object value, ComparisonOperation operation) {
        return switch (operation) {
            case LIKE -> builder.like(builder.lower(root.get(fieldName)), "%" + value.toString().toLowerCase() + "%");
            case EQUAL -> builder.equal(root.get(fieldName), value);
            case GREATER_THAN -> builder.greaterThan(root.get(fieldName), (Comparable<Object>) value);
            case LESS_THAN -> builder.lessThan(root.get(fieldName), (Comparable<Object>) value);
        };
    }
}
