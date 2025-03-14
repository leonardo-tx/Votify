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

# Configurações do banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/votifydb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC
spring.datasource.username=seu_usuário
spring.datasource.password=sua_senha
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=create # Para testes usar o create, fora desse ambiente, utilize "update".
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Configurações do aplicativo
app.token.access-token-max-age=900 # Tempo em segundos
app.token.refresh-token-max-age=2419200 # Tempo em segundos
app.token.refresh-token-secret=sua_chave_para_o_refresh_token
app.cookie.http-only=true
app.cookie.secure=true
app.cookie.path=/
app.password-reset.expiration-minutes=15
```
### 3.3 Build do Projeto
#### Na raiz do projeto, execute:
```
mvn clean install
```
</details>

<details>
  <summary><strong>4. Executar a API</strong></summary>

#### Entre no diretório raiz do projeto:
```
mvn clean install -DskipTests
mvn spring-boot:run -pl votify-api
```
</details>

<details>
  <summary><strong>5. Executar o Console</strong></summary>

#### Entre no diretório raiz do projeto:
```
mvn clean install -DskipTests
mvn clean compile exec:java -pl votify-console
```
</details>
