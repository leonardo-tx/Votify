# Votify
![Votify CI](https://github.com/leonardo-tx/Votify/actions/workflows/java-ci.yml/badge.svg)
![Line Coverage](https://raw.githubusercontent.com/leonardo-tx/Votify/refs/heads/badges/.github/badges/jacoco.svg)
![Branch Coverage](https://raw.githubusercontent.com/leonardo-tx/Votify/refs/heads/badges/.github/badges/branches.svg)

O Votify é um projeto em grupo desenvolvido na faculdade Instituto Infnet  
no bloco de Engenharia Disciplinada de Softwares.

O sistema tem como objetivo permitir enquetes / votações online de maneira  
fácil e prática.

## Membros

- André Augusto Ferrarez
- Bruno Thales dos Santos
- Jonathan de Oliveira Scottini
- José Luiz Barbosa Costa
- Leonardo Rego Teixeira
- Luís Miguel Silva Amorim
- Maitê Mota Belo de Souza Silva
- Victor Matheus Paiva Vianna

<details>
  <summary><strong>1. Visão Geral do Projeto</strong></summary>

O Votify é composto por um sistema modular com os seguintes módulos:

- **votify-api**: Responsável pelos endpoints REST da aplicação.
- **votify-core**: Contém a lógica de domínio, entidades, repositórios e serviços.
- **votify-console**: (Opcional) Aplicação de console para testar chamadas à API.

</details>

<details>
  <summary><strong>2. Pré-requisitos e Dependências</strong></summary>

Para rodar o projeto, é necessário ter instalado:

- **Java 17** (JDK 17)
- **Maven** (para build e gerenciamento de dependências)
- **MySQL** (para o banco de dados; certifique-se de ter um schema, por exemplo, `votifydb`)
- **Git** (para controle de versão)

</details>

<details>
  <summary><strong>3. Instalação e Configuração</strong></summary>

### 3.1 Clonando o Repositório
```bash
git clone https://github.com/leonardo-tx/Votify.git
cd votify
```

### 3.2 Configurando o Banco de Dados
#### Criar e Adicionar ao arquivo application.properties com as configurações abaixo
#### Criar ele dentro do seguinte path "votify-api/src/main/resources"

```
#Configurações da aplicação
spring.application.name=votify-api
server.port=sua_porta

#Configurações do banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/votify?createDatabaseIfNotExist=true&useTimezone=true&serverTimezone=GMT
spring.datasource.username=seu_usuário
spring.datasource.password=sua_senha
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
#Para is primeiros passos usar o creat, depois atualizar para o "update".
spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```
### 3.3 Build do Projeto
#### Na raiz do projeto, execute:
```
mvn clean install
```
</details>

<details>
  <summary><strong>4. Executar a API</strong></summary>

#### Entre no diretório votify-api e execute:
```
cd votify-api
mvn spring-boot:run
```
</details>
