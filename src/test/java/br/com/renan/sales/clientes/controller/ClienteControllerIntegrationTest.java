package br.com.renan.sales.clientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveExecutarCrudEPreservarCpf() throws Exception {
        String cadastro = """
                {
                  "nome": "Cliente Teste",
                  "cpf": "12345678901",
                  "tel": "11999999999",
                  "email": "cliente.teste@example.com",
                  "endereco": "Rua das Flores",
                  "numero": 10,
                  "cidade": "São Paulo",
                  "estado": "SP"
                }
                """;

        MockHttpServletResponse respostaCadastro = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cadastro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andReturn()
                .getResponse();

        long id = objectMapper.readTree(respostaCadastro.getContentAsString())
                .get("id")
                .asLong();

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cliente Teste"));

        mockMvc.perform(get("/clientes/is-cadastrado/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/clientes")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cpf").value("12345678901"));

        String atualizacao = """
                {
                  "nome": "Cliente Atualizado",
                  "cpf": "99999999999",
                  "tel": "11888888888",
                  "email": "cliente.atualizado@example.com",
                  "endereco": "Rua Nova",
                  "numero": 20,
                  "cidade": "São Paulo",
                  "estado": "SP"
                }
                """;

        mockMvc.perform(put("/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atualizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("Cliente Atualizado"));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.email").value("cliente.atualizado@example.com"));

        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/clientes/{id}", id))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/clientes/is-cadastrado/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void deveRetornar400ParaDadosInvalidosE404ParaIdInexistente() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nome").exists())
                .andExpect(jsonPath("$.errors.cpf").exists());

        mockMvc.perform(get("/clientes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar409ParaCpfDuplicado() throws Exception {
        String cadastro = """
                {
                  "nome": "Cliente Duplicado",
                  "cpf": "22222222222",
                  "tel": "11999999999",
                  "email": "duplicado@example.com",
                  "endereco": "Rua A",
                  "numero": 10,
                  "cidade": "São Paulo",
                  "estado": "SP"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cadastro))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cadastro))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deveRetornar409ParaEmailDuplicado() throws Exception {
        String primeiroCadastro = """
                {
                  "nome": "Primeiro Cliente",
                  "cpf": "33333333333",
                  "tel": "11999999999",
                  "email": "email.duplicado@example.com",
                  "endereco": "Rua A",
                  "numero": 10,
                  "cidade": "São Paulo",
                  "estado": "SP"
                }
                """;

        String segundoCadastro = """
                {
                  "nome": "Segundo Cliente",
                  "cpf": "44444444444",
                  "tel": "11999999999",
                  "email": "email.duplicado@example.com",
                  "endereco": "Rua B",
                  "numero": 20,
                  "cidade": "São Paulo",
                  "estado": "SP"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(primeiroCadastro))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(segundoCadastro))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
