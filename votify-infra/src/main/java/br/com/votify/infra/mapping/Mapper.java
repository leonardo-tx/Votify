package br.com.votify.infra.mapping;

public interface Mapper<M, E> {
    M toModel(E entity);
    E toEntity(M model);
}
