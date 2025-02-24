package br.com.votify.api.dto;

public interface DTOInput<TEntity> {
    TEntity convertToEntity();
}
