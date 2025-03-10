package br.com.votify.dto;

public interface DTOInput<TEntity> {
    TEntity convertToEntity();
}
