<h1 align="center">SkyDrinks API</h1>

<p align="center">
    SkyDrinksAPI é uma API construida para uma empresa fictícia(Sky Drinks), desenvolvida com Spring Boot.   
</p>

<p align="center">
    <img width="200" src="./SkyDrinksAPI.png" />
</p>

## :wrench: Como executar?

### :mag_right: Requisitos:

* Docker e docker-compose.
* Java 11 ou superior.
* Maven(opcional pois no projeto já vem um binário do mesmo).

### :athletic_shoe: Passo a passo:

1. Clone este repositório para sua máquina e abra o terminal já no diretório do projeto. 
2. Utilize o comando `docker-compose up`, para iniciar o container do MySQL.
3. Utilize o comando `mvn clean package` para gerar um *.jar* do projeto.
4. Utilize o comando `mvn spring-boot:run` para iniciar o servidor.
5. Para o serviço de restauração de senhas funcionar, você deve configurar as credenciais corretamente:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.mail.username=SEU_USERNAME --spring.mail.password=SUA_SENHA"
```

Pronto, caso tudo tenha ocorrido com sucesso, o projeto funcionará normalmente!

### :paperclip: Informaçõea adicionais:

* Durante a geração do *.jar*, você pode escolher se quer executar todos os testes, os testes unitários, os testes de integração ou nenhum teste:

```bash
mvn clean package -P all-tests // Para executar todos os testes.
mvn clean package -P unit-tests // Para executar os testes unitários.
mvn clean package -P integration-tests // Para executar os testes de integração. 
mvn clean package -P skip-tests // Para não executar nenhum teste.
```

* Caso precise mudar de banco de dados, você pode alterar a configuração do *docker-compsose.yml*

* Caso a tabela de usuários esteja vazia, será gerado um usuário com acesso de admin padrão, ele servirá para você registrar novos usuários, caso deseje, você pode criar um novo usuário admin com ele, e então, apagá-lo.

```json
{
  "email": "admin@mail.com",
  "password": "admin123",
  "name": "Admin",
  "role": "USER,BARMEN,WAITER,ADMIN",
  "cpf": "878.711.897-19",
  "birthAt": "2000-04-04"
}
```

Caso você precise de alguns dados de bebidas para testar, e não queria pensar em tudo, você pode pegar [aqui](https://github.com/SkyG0D/sky-drinks-api/releases/tag/v1.0.0).

## :rocket: Tecnologias

* [Spring Boot](https://spring.io/projects/spring-boot) - Framework Java para criação de APIs REST.

* [Project Lombok](https://projectlombok.org/) - Biblioteca Java que ajuda a remover a verbosidade do nosso código usando anotações.

* [Map Struct](https://mapstruct.org/) - Framework Java para mapear DTOs.

* [JUnit5](https://junit.org/junit5/) - Framework Java para testes unitários.

* [H2](http://www.h2database.com/html/features.html) - Banco de dados em memória.

* [Postgres](https://www.postgresql.org/) - Banco de dados relacional(usado aqui no ambiente de produção).

* [MySQL](https://www.mysql.com/) - Banco de dados relacional(usado aqui no ambiente de desenvolvimento).

* [Nimbus JOSE](https://mvnrepository.com/artifact/com.nimbusds/nimbus-jose-jwt) Biblioteca para uso de JSON Web Tokens.

<h3 align="center">
    <a href="https://github.com/SkyG0D/sky-drinks-ui">Repositório da parte do site</a>
</h3>
