package com.iss.eventorium.solution.specifications;

import com.iss.eventorium.solution.models.Memento;
import org.springframework.data.jpa.domain.Specification;

public class MementoSpecification {

    private MementoSpecification() {}

    public static Specification<Memento> hasSolution(Long solutionId) {
        return (root, query, cb) ->
                cb.equal(root.get("solution").get("id"), solutionId);
    }

    public static Specification<Memento> filterBySolution(Long solutionId) {
        return (root, query, cb) -> {
            assert query != null;
            query.orderBy(cb.desc(root.get("validFrom")));
            return cb.equal(root.get("solution").get("id"), solutionId);
        };
    }
}