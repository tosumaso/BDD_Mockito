package guru.springframework.sfgpetclinic.services.springdatajpa;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.repositories.VisitRepository;

@ExtendWith(MockitoExtension.class)
class VisitSDJpaServiceTest {

    @Mock
    VisitRepository visitRepository;

    @InjectMocks
    VisitSDJpaService service;

    @DisplayName("Test Find All")
    @Test
    void findAll() {
    	//given
        Visit visit = new Visit();
        Set<Visit> visits = new HashSet<>();
        visits.add(visit);
        given(visitRepository.findAll()).willReturn(visits);
        
        //when
        Set<Visit> foundVisits = service.findAll();

        //then
        then(visitRepository).should().findAll();
        assertThat(foundVisits).hasSize(1);

    }

    @Test
    void findById() {
    	//given
        Visit visit = new Visit();
        given(visitRepository.findById(1l)).willReturn(Optional.of(visit));

        //when
        Visit foundVisit = service.findById(1L);

        //then
        then(visitRepository).should().findById(1l);

        assertThat(foundVisit).isNotNull();
    }

    @Test
    void save() {
    	//given
        Visit visit = new Visit();
        given(visitRepository.save(any(Visit.class))).willReturn(visit);

        //when
        Visit savedVisit = service.save(new Visit());

        //then
        then(visitRepository).should().save(any(Visit.class));

        assertThat(savedVisit).isNotNull();
    }

    @Test
    void delete() {
    	//given
        Visit visit = new Visit();

        //when
        service.delete(visit);

        //then
        then(visitRepository).should().delete(visit);

    }

    @Test
    void deleteById() {

    	//when
        service.deleteById(1L);

        //then
        then(visitRepository).should().deleteById(anyLong());
    }
}