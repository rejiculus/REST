package org.example.repository.imp;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.entity.Barista;
import org.example.entity.exception.BaristaNotFoundException;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class BaristaRepositoryImpTest {

    private static BaristaRepository baristaRepository;
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withInitScript("DB_script.sql");

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        ConnectionManager connectionManager = new ConnectionManagerImp(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        baristaRepository = new BaristaRepositoryImp(connectionManager);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void createTest() {
        Barista barista = new Barista("fullName");

        Barista resultBarista = baristaRepository.create(barista);

        assertNotNull(resultBarista.getId());
        assertEquals(barista.getFullName(), resultBarista.getFullName());
        assertEquals(barista.getTipSize(), resultBarista.getTipSize());
    }

    @Test
    void createWrongTest() {
        assertThrows(NullParamException.class, () -> baristaRepository.create(null));
    }

    @Test
    void updateTest() {

        Barista barista = new Barista("Arlekino", 0.99);
        barista = baristaRepository.create(barista);

        barista.setTipSize(0.1);

        Barista resultBarista = baristaRepository.update(barista);

        assertEquals(barista.getId(), resultBarista.getId());
        assertEquals(barista.getFullName(), resultBarista.getFullName());
        assertEquals(barista.getTipSize(), resultBarista.getTipSize());
        assertEquals(barista.getOrderList(), resultBarista.getOrderList());
    }

    @Test
    void updateWrongTest() {

        Barista barista = new Barista("Arlekino", 0.99);

        assertThrows(NoValidIdException.class, () -> baristaRepository.update(barista));
        assertThrows(NullParamException.class, () -> baristaRepository.update(null));
    }

    @Test
    void deleteTest() {
        Barista barista = new Barista("fullName");

        Barista resultBarista = baristaRepository.create(barista);
        baristaRepository.delete(resultBarista.getId());

        assertEquals(Optional.empty(), baristaRepository.findById(resultBarista.getId()));
    }

    @Test
    void deleteWrongTest() {
        Assertions.assertThrows(BaristaNotFoundException.class, () -> baristaRepository.delete(99L));
        Assertions.assertThrows(NoValidIdException.class, () -> baristaRepository.delete(-1L));
        Assertions.assertThrows(NullParamException.class, () -> baristaRepository.delete(null));
    }

    @Test
    void findAllByPageTest() {
        baristaRepository.create(new Barista("John Doe"));
        baristaRepository.create(new Barista("John Doe"));
        baristaRepository.create(new Barista("John Doe"));

        List<Barista> baristaList = baristaRepository.findAllByPage(0, 1);

        assertEquals(1, baristaList.size());
        assertEquals(List.of(1L), baristaList.stream().map(Barista::getId).toList());

        baristaList = baristaRepository.findAllByPage(0, 2);

        assertEquals(2, baristaList.size());
        assertEquals(List.of(1L, 2L), baristaList.stream().map(Barista::getId).toList());

        baristaList = baristaRepository.findAllByPage(2, 1);

        assertEquals(1, baristaList.size());
        assertEquals(List.of(3L), baristaList.stream().map(Barista::getId).toList());
    }

    @Test
    void findAllByPageWrongTest() {

        assertThrows(NoValidPageException.class, () -> baristaRepository.findAllByPage(-1, 1));
        assertThrows(NoValidLimitException.class, () -> baristaRepository.findAllByPage(0, 0));
        assertThrows(NoValidLimitException.class, () -> baristaRepository.findAllByPage(0, -1));
    }

    @Test
    void findAllTest() {
        Barista barista = new Barista("fullName");
        baristaRepository.create(barista);
        baristaRepository.create(barista);
        baristaRepository.create(barista);

        List<Barista> baristaList = baristaRepository.findAll();

        assertNotNull(baristaList);
        assertTrue(baristaList.size() >= 3);
    }

    @Test
    void findByIdTest() {
        Barista expectedBarista = new Barista("John Doe", new ArrayList<>(), 0.9);
        expectedBarista = baristaRepository.create(expectedBarista);

        Optional<Barista> resultBarista = baristaRepository.findById(expectedBarista.getId());

        assertNotEquals(Optional.empty(), resultBarista);
        assertEquals(Optional.of(expectedBarista), resultBarista);
    }

    @Test
    void findByIdWrongTest() {

        assertThrows(NullParamException.class, () -> baristaRepository.findById(null));
        assertThrows(NoValidIdException.class, () -> baristaRepository.findById(-1L));
    }
}