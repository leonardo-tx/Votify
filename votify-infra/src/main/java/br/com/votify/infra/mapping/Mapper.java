package br.com.votify.infra.mapping;

import java.util.Optional;

public interface Mapper<TModel, TEntity> {
    default Optional<TModel> parseToOptionalModel(Optional<TEntity> entity) {
        return entity.map(this::parseToModel);
    }

    default Optional<TEntity> parseToOptionalEntity(Optional<TModel> model) {
        return model.map(this::parseToEntity);
    }

    TModel parseToModel(TEntity entity);
    TEntity parseToEntity(TModel model);
}
