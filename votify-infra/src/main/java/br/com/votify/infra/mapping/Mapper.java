package br.com.votify.infra.mapping;

import jakarta.transaction.NotSupportedException;

public interface Mapper<M, E> {
    M toModel(E entity);
    E toEntity(M model) throws NotSupportedException;
}
