package org.example.service.imp;

import org.example.entity.Barista;
import org.example.entity.exception.NoValidIdException;
import org.example.entity.exception.NoValidNameException;
import org.example.entity.exception.NoValidTipSizeException;
import org.example.entity.exception.NullParamException;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.service.exception.BaristaAlreadyExistException;
import org.example.service.exception.BaristaNotFoundException;
import org.example.servlet.dto.BaristaNoRefDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaristaServiceTest {
    static AutoCloseable mocks;
    @Spy
    BaristaRepository baristaRepository;
    @Spy
    OrderRepository orderRepository;
    @Spy
    CoffeeRepository coffeeRepository;

    @InjectMocks
    BaristaService baristaService;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    public static void close() throws Exception {
        mocks.close();
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
        Barista specifiedBarista = Mockito.mock(Barista.class);
        BaristaNoRefDTO specifiedBaristaDTO = Mockito.mock(BaristaNoRefDTO.class);

        Barista expectedBarista = new Barista("John Doe");
        expectedBarista.setId(99L);

        Mockito.when(specifiedBaristaDTO.toBarista(orderRepository))
                .thenReturn(specifiedBarista);
        Mockito.when(baristaRepository.create(specifiedBarista))
                .thenReturn(expectedBarista);


        Barista resultBarista = baristaService.create(specifiedBaristaDTO);

        assertEquals(expectedBarista, resultBarista);
    }

    @Test
    void createWrongTest() {
        Barista specifiedBarista = Mockito.mock(Barista.class);
        BaristaNoRefDTO baristaDTONull = Mockito.mock(BaristaNoRefDTO.class);
        BaristaNoRefDTO baristaDTONoValidId = Mockito.mock(BaristaNoRefDTO.class);
        BaristaNoRefDTO baristaDTONoValidName = Mockito.mock(BaristaNoRefDTO.class);
        BaristaNoRefDTO baristaDTONoValidTip = Mockito.mock(BaristaNoRefDTO.class);
        BaristaNoRefDTO baristaDTOExist = Mockito.mock(BaristaNoRefDTO.class);


        Mockito.when(baristaDTONull.toBarista(orderRepository))
                .thenThrow(NullParamException.class);
        Mockito.when(baristaDTONoValidId.toBarista(orderRepository))
                .thenThrow(NoValidIdException.class);
        Mockito.when(baristaDTONoValidName.toBarista(orderRepository))
                .thenThrow(NoValidNameException.class);
        Mockito.when(baristaDTONoValidTip.toBarista(orderRepository))
                .thenThrow(NoValidTipSizeException.class);
        Mockito.when(baristaDTOExist.toBarista(orderRepository))
                .thenReturn(specifiedBarista);
        Mockito.when(baristaRepository.create(specifiedBarista))
                .thenThrow(new BaristaAlreadyExistException(0L));

        Assertions.assertThrows(NullParamException.class, () -> baristaService.create(baristaDTONull));
        Assertions.assertThrows(NullParamException.class, () -> baristaService.create(null));
        Assertions.assertThrows(NoValidIdException.class, () -> baristaService.create(baristaDTONoValidId));
        Assertions.assertThrows(NoValidNameException.class, () -> baristaService.create(baristaDTONoValidName));
        Assertions.assertThrows(NoValidTipSizeException.class, () -> baristaService.create(baristaDTONoValidTip));
        Assertions.assertThrows(BaristaAlreadyExistException.class, () -> baristaService.create(baristaDTOExist));
    }

    @Test
    void updateTest() {
        Barista specificBarista = Mockito.mock(Barista.class);
        BaristaNoRefDTO baristaDTO = Mockito.mock(BaristaNoRefDTO.class);

        Mockito.when(baristaDTO.toBarista(orderRepository))
                .thenReturn(specificBarista);
        Mockito.when(baristaRepository.update(specificBarista))
                .thenReturn(specificBarista);

        Barista resultBarista = baristaService.update(baristaDTO);

        assertEquals(specificBarista, resultBarista);
    }

    @Test
    void updateWrongTest() {
        Barista specifiedBarista = Mockito.mock(Barista.class);
        BaristaNoRefDTO baristaDTONull = Mockito.mock(BaristaNoRefDTO.class);
        BaristaNoRefDTO baristaDTONoValidId = Mockito.mock(BaristaNoRefDTO.class);
        BaristaNoRefDTO baristaDTONoValidName = Mockito.mock(BaristaNoRefDTO.class);
        BaristaNoRefDTO baristaDTONoValidTip = Mockito.mock(BaristaNoRefDTO.class);
        BaristaNoRefDTO baristaDTONotFound = Mockito.mock(BaristaNoRefDTO.class);


        Mockito.when(baristaDTONull.toBarista(orderRepository))
                .thenThrow(NullParamException.class);
        Mockito.when(baristaDTONoValidId.toBarista(orderRepository))
                .thenThrow(NoValidIdException.class);
        Mockito.when(baristaDTONoValidName.toBarista(orderRepository))
                .thenThrow(NoValidNameException.class);
        Mockito.when(baristaDTONoValidTip.toBarista(orderRepository))
                .thenThrow(NoValidTipSizeException.class);
        Mockito.when(baristaDTONotFound.toBarista(orderRepository))
                .thenReturn(specifiedBarista);
        Mockito.when(baristaRepository.update(specifiedBarista))
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
        //todo check id not found exception
        Assertions.assertThrows(NullParamException.class, () -> baristaService.delete(null));
        Assertions.assertThrows(NoValidIdException.class, () -> baristaService.delete(-1L));
    }
}