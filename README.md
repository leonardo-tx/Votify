# Votify
![Votify CI](https://github.com/leonardo-tx/Votify/actions/workflows/java-ci.yml/badge.svg)
![Line Coverage](https://raw.githubusercontent.com/leonardo-tx/Votify/refs/heads/badges/.github/badges/jacoco.svg)
![Branch Coverage](https://raw.githubusercontent.com/leonardo-tx/Votify/refs/heads/badges/.github/badges/branches.svg)

O Votify é um projeto em grupo desenvolvido na faculdade Instituto Infnet
no bloco de Engenharia Disciplinada de Softwares.

O sistema tem como objetivo permitir enquetes / votações online de maneira
fácil e prática.

## Membros

### Product Owner / Orientador
- Leonardo Silva da Gloria

### Team Leader / Analista de Negócios
- Leonardo Rego Teixeira

### Arquiteto
- Victor Matheus Paiva Vianna

### Team Members
- André Augusto Ferrarez
- Bruno Thales dos Santos
- Jonathan de Oliveira Scottini
- José Luiz Barbosa Costa
- Luís Miguel Silva Amorim
- Maitê Mota Belo de Souza Silva

<details>
  <summary><strong>1. Visão Geral do Projeto</strong></summary>

O Votify é composto por um sistema modular com os seguintes módulos:

- **votify-api**: Responsável pelos endpoints REST da aplicação.
- **votify-core**: Contém a lógica de domínio, entidades, repositórios e serviços.
- **votify-dto**: Contém as DTO's utilizadas pela API.
- **votify-test**: Onde há todos os testes do projeto.
- **votify-web**: Aplicação web.

</details>

<details>
  <summary><strong>2. Pré-requisitos e Dependências</strong></summary>

Para rodar o projeto, é necessário ter instalado:

- **Java 17** (JDK 17)
- **Maven** (para build e gerenciamento de dependências)
- **MySQL** (para o banco de dados; certifique-se de ter um schema, por exemplo, `votifydb`)
- **Git** (para controle de versão)
- **Yarn** ou **NPM** (para executar o projeto web)

</details>

<details>
  <summary><strong>3. Instalação e Configuração</strong></summary>

### 3.1 Clonando o Repositório
```bash
git clone https://github.com/leonardo-tx/Votify.git
cd Votify
```

### 3.2 Configurando o Banco de Dados
#### No projeto há três template de configurações do projeto:
- application.properties (Afeta a configuração geral)
- application-dev.properties (Afeta a configuração apenas do ambiente de desenvolvimento)
- application-prod.properties (Afeta a configuração apenas do ambiente de produção)

#### Também há 2 arquivos SQL, para colocar dados no início:
- data-dev.sql (Afeta os dados iniciais apenas do ambiente de desenvolvimento)
- data-prod.sql (Afeta os dados iniciais apenas do ambiente de produção)

Todos os arquivos se encontram em votify-api/src/main/resources

### 3.3 Build do Projeto
#### Na raiz do projeto, execute:
```bash
mvn clean install # Adicione o parâmetro -DskipTests para pular os testes
```
</details>

<details>
  <summary><strong>4. Executar a API</strong></summary>

#### Entre no diretório raiz do projeto:
```bash
mvn clean install -DskipTests
mvn spring-boot:run -pl votify-api
```
</details>

<details>
  <summary><strong>5. Executar o Servidor WEB</strong></summary>

#### Entre no diretório raiz do projeto:
```bash
cd votify-web
yarn install # Ou npm install
yarn run build # Ou npm run build
yarn run start # Ou npm run start
```
</details>
