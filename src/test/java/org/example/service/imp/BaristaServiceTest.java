package org.example.service.imp;

import org.example.db.DatabaseConfig;
import org.example.entity.Barista;
import org.example.entity.exception.*;
import org.example.repository.BaristaRepository;
import org.example.repository.OrderRepository;
import org.example.repository.exception.NoValidLimitException;
import org.example.repository.exception.NoValidPageException;
import org.example.service.mapper.BaristaDtoToBaristaMapper;
import org.example.servlet.dto.BaristaCreateDTO;
import org.example.servlet.dto.BaristaUpdateDTO;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class BaristaServiceTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:alpine3.20")
            .withCopyFileToContainer(MountableFile.forClasspathResource("DB_script.sql"),
                    "/docker-entrypoint-initdb.d/01-schema.sql");
    static AutoCloseable mocks;
    @Mock
    static BaristaDtoToBaristaMapper mapper;
    @Mock
    BaristaRepository baristaRepository;
    @Mock
    OrderRepository orderRepository;

    BaristaService baristaService;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        DatabaseConfig.setDbUrl(postgres.getJdbcUrl());
        DatabaseConfig.setUsername(postgres.getUsername());
        DatabaseConfig.setPassword(postgres.getPassword());
    }

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        baristaService = new BaristaService(baristaRepository, orderRepository);
    }

    @AfterAll
    public static void close() throws Exception {
        mocks.close();
        postgres.stop();
    }


    @Test
    void constructorsTest() {
        Connection connection = Mockito.mock(Connection.class);
        Assertions.assertDoesNotThrow(() -> new BaristaService(baristaRepository, orderRepository));
        Assertions.assertDoesNotThrow(() -> new BaristaService(connection));
        Assertions.assertThrows(NullParamException.class, () -> new BaristaService(null, orderRepository));
        Assertions.assertThrows(NullParamException.class, () -> new BaristaService(baristaRepository, null));
        Assertions.assertThrows(NullParamException.class, () -> new BaristaService(null));
    }

    @Test
    void findAllTest() {
        List<Barista> specifiedBaristaList = new ArrayList<>(List.of(
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class)
        ));

        Mockito.when(baristaRepository.findAll())
                .thenReturn(specifiedBaristaList);

        List<Barista> resultBatistaList = baristaService.findAll();

        assertEquals(specifiedBaristaList, resultBatistaList);
    }

    @Test
    void findByIdTest() {
        Long specifiedId = 1L;
        Barista specifiedBarista = Mockito.mock(Barista.class);

        Mockito.when(baristaRepository.findById(specifiedId))
                .thenReturn(Optional.of(specifiedBarista));

        Barista resultBarista = baristaService.findById(specifiedId);

        assertEquals(specifiedBarista, resultBarista);
    }

    @Test
    void findByIdWrongTest() {
        Long specifiedId = 1L;

        Mockito.when(baristaRepository.findById(specifiedId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BaristaNotFoundException.class, () -> baristaService.findById(specifiedId));
        Assertions.assertThrows(NullParamException.class, () -> baristaService.findById(null));
        Assertions.assertThrows(NoValidIdException.class, () -> baristaService.findById(-1L));
    }

    @Test
    void createTest() {
        BaristaCreateDTO specifiedBaristaDTO = Mockito.spy(new BaristaCreateDTO("John Doe", 0.1));

        Barista expectedBarista = new Barista("John Doe");
        expectedBarista.setId(99L);

        Mockito.when(baristaRepository.create(Mockito.argThat(barista -> barista.getFullName().equals(expectedBarista.getFullName()))))
                .thenReturn(expectedBarista);

        Barista resultBarista = baristaService.create(specifiedBaristaDTO);

        assertEquals(expectedBarista, resultBarista);
    }

    @Test
    void createWrongTest() {

        BaristaCreateDTO baristaDTONull = Mockito.spy(new BaristaCreateDTO(null, 0.1));
        BaristaCreateDTO baristaDTONoValidName = Mockito.spy(new BaristaCreateDTO("", 0.1));
        BaristaCreateDTO baristaDTONoValidTip = Mockito.spy(new BaristaCreateDTO("John Doe", -0.1));


        Assertions.assertThrows(NullParamException.class, () -> baristaService.create(baristaDTONull));
        Assertions.assertThrows(NullParamException.class, () -> baristaService.create(null));
        Assertions.assertThrows(NoValidNameException.class, () -> baristaService.create(baristaDTONoValidName));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> baristaService.create(baristaDTONoValidTip));
    }

    @Test
    void updateTest() {
        Barista specificBarista = Mockito.mock(Barista.class);
        BaristaUpdateDTO baristaDTO = Mockito.spy(new BaristaUpdateDTO(0L, "John Doe", 0.1, List.of()));

        Mockito.when(baristaRepository.update(Mockito.argThat(barista -> barista.getFullName().equals(baristaDTO.fullName()))))
                .thenReturn(specificBarista);

        Barista resultBarista = baristaService.update(baristaDTO);

        assertEquals(specificBarista, resultBarista);
    }

    @Test
    void updateWrongTest() {
        BaristaUpdateDTO baristaDTONull = new BaristaUpdateDTO(null, "John Doe", 0.1, List.of());
        BaristaUpdateDTO baristaDTONoValidId = new BaristaUpdateDTO(-1L, "John Doe", 0.1, List.of());
        BaristaUpdateDTO baristaDTONoValidName = new BaristaUpdateDTO(0L, "", 0.1, List.of());
        BaristaUpdateDTO baristaDTONoValidTip = new BaristaUpdateDTO(0L, "John Doe", -0.1, List.of());
        BaristaUpdateDTO baristaDTONotFound = new BaristaUpdateDTO(0L, "John Doe", 0.1, List.of());

        Mockito.when(baristaRepository.update(Mockito.argThat(barista -> barista.getId().equals(0L))))
                .thenThrow(BaristaNotFoundException.class);

        Assertions.assertThrows(NullParamException.class, () -> baristaService.update(baristaDTONull));
        Assertions.assertThrows(NullParamException.class, () -> baristaService.update(null));
        Assertions.assertThrows(NoValidIdException.class, () -> baristaService.update(baristaDTONoValidId));
        Assertions.assertThrows(NoValidNameException.class, () -> baristaService.update(baristaDTONoValidName));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> baristaService.update(baristaDTONoValidTip));

        Assertions.assertThrows(BaristaNotFoundException.class, () -> baristaService.update(baristaDTONotFound));

    }

    @Test
    void deleteTest() {
        Long specifiedId = 0L;

        Assertions.assertDoesNotThrow(() -> baristaService.delete(specifiedId));
        Mockito.verify(baristaRepository).delete(specifiedId);
    }

    @Test
    void deleteWrongTest() {
        baristaService.delete(0L);
        Mockito.verify(baristaRepository, Mockito.times(1)).delete(0L);
        Assertions.assertThrows(NullParamException.class, () -> baristaService.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> baristaService.delete(-1L));
    }


    @Test
    void findAllByPageTest() {
        List<Barista> specifiedBaristaListPage0 = new ArrayList<>(List.of(
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class)
        ));
        List<Barista> specifiedBaristaListPage1 = new ArrayList<>(List.of(
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class),
                Mockito.mock(Barista.class)
        ));

        Mockito.when(baristaRepository.findAllByPage(0, 4))
                .thenReturn(specifiedBaristaListPage0);

        Mockito.when(baristaRepository.findAllByPage(1, 4))
                .thenReturn(specifiedBaristaListPage1);

        List<Barista> resultBaristaList = baristaService.findAllByPage(0, 4);
        assertEquals(specifiedBaristaListPage0, resultBaristaList);

        resultBaristaList = baristaService.findAllByPage(1, 4);
        assertEquals(specifiedBaristaListPage1, resultBaristaList);
    }

    @Test
    void findAllByPageWrongTest() {
        Assertions.assertThrows(NoValidPageException.class, () -> baristaService.findAllByPage(-1, 1));
        Assertions.assertThrows(NoValidLimitException.class, () -> baristaService.findAllByPage(0, 0));
        Assertions.assertThrows(NoValidLimitException.class, () -> baristaService.findAllByPage(0, -1));
    }
}