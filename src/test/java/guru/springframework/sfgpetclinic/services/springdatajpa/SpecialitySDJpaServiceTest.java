package guru.springframework.sfgpetclinic.services.springdatajpa;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

	//lenient = true : unnecessary stubs(読み込まれないテストコード)と,
	//stubbing argument mismatch(when,givenで指定した引数と実際の引数の値が異なる)の探知をオフにする
    @Mock(lenient = true)
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialitySDJpaService service;
    
    @Test
    void testDeleteByObject() {
    	//given
        Speciality speciality = new Speciality();

        //when
        service.delete(speciality);

        //then
        then(specialtyRepository).should().delete(any(Speciality.class));
//        verify(specialtyRepository).delete(any(Speciality.class));
    }
    
    @Test
    void findByIdBddTest() { //BDD: Behaviour-Test-Driven
    	//given
    	Speciality speciality = new Speciality();
    	//given(methodCall).willReturn(value) : when,thenReturnと同じ,mockメソッドを呼び出したら指定した値が返るように設定
    	given(specialtyRepository.findById(1l)).willReturn(Optional.of(speciality));
    	
    	//when
    	Speciality foundSpecialty = service.findById(1L);
    
    	//then
        assertThat(foundSpecialty).isNotNull(); 
        then(specialtyRepository).should().findById(1l);
        //shouldHaveNoMoreInteractions(): これ以上mockオブジェクトの呼び出しを行わないことを検証
        then(specialtyRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteById() {
    	//given none
    	
    	//when
        service.deleteById(1l);
        service.deleteById(1l);

        //then
        then(specialtyRepository).should(times(2)).deleteById(1l);
//        verify(specialtyRepository, times(2)).deleteById(1l);
    }

    @Test
    void deleteByIdAtLeast() {
    	//given none
    	
    	//when
        service.deleteById(1l);
        service.deleteById(1l);
     
        //then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1l);
//        verify(specialtyRepository, atLeastOnce()).deleteById(1l);
    }

    @Test
    void deleteByIdAtMost() {
    	
    	//when
        service.deleteById(1l);
        service.deleteById(1l);

        //then
        then(specialtyRepository).should(atMost(5)).deleteById(1l);
//        verify(specialtyRepository, atMost(5)).deleteById(1l);
    }

    @Test
    void deleteByIdNever() {
    	
    	//when
        service.deleteById(1l);
        service.deleteById(1l);
        
        //then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1l);
        then(specialtyRepository).should(never()).deleteById(5l);
//        verify(specialtyRepository, atLeastOnce()).deleteById(1l);
//        verify(specialtyRepository, never()).deleteById(5L);
    }

    @Test
    void testDelete() {
    	//when
        service.delete(new Speciality());
        
        then(specialtyRepository).should().delete(any(Speciality.class));
    }
    
    @Test
    void testThrow() {
    	// 一番オーソドックスな例外処理
    	doThrow(new RuntimeException("bbb")).when(specialtyRepository).delete(any());
    	assertThrows(RuntimeException.class, () -> specialtyRepository.delete(new Speciality()));
    	verify(specialtyRepository).delete(any());
    }
    
    @Test
    void testFindByIDThrows() {
    	//BDD-Mockitoを使った例外処理（戻り値あり）
    	given(specialtyRepository.findById(1l)).willThrow(new RuntimeException("ccc"));
    	assertThrows(RuntimeException.class, ()-> specialtyRepository.findById(1l));
    	then(specialtyRepository).should().findById(1l);
    }
    
    @Test
    void testDeleteBDD() {
    	//BDD-Mockitoを使った例外処理(戻り値なし)
    	willThrow(new RuntimeException("ddd")).given(specialtyRepository).delete(any());
    	assertThrows(RuntimeException.class,()-> specialtyRepository.delete(new Speciality()));
    	then(specialtyRepository).should().delete(any(Speciality.class));
    }
    
    @Test
    void testSaveLambda() {
    	//given
    	final String MATCH_ME = "MATCH_ME";
    	Speciality speciality = new Speciality();
    	speciality.setDescription(MATCH_ME);
    	Speciality savedSpecialty = new Speciality();
    	savedSpecialty.setId(1l);
    	
    	//mockMethod(argThat( arg -> )).willReturn : メソッドの引数のフィールドが一致したら指定した戻り値を返す
    	given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpecialty);
    	
    	//when
    	Speciality returnedSpeciality = service.save(speciality);
    	
    	//then
    	assertThat(returnedSpeciality.getId()).isEqualTo(1l);
    }
    
    @Test
    void testSaveLambdaNoMatch() {
    	//given
    	final String MATCH_ME = "MATCH_ME";
    	Speciality speciality = new Speciality();
    	speciality.setDescription("Not a match");
    	Speciality savedSpecialty = new Speciality();
    	savedSpecialty.setId(1l);
    	
    	//argThatのラムダ式がfalseの場合、実行時エラーが発生する。mockオブジェクトの設定を@Mock(lenient = true)に変更する
    	given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpecialty);
    	
    	//when
    	Speciality returnedSpeciality = service.save(speciality);
    	
    	//then
    	assertNull(returnedSpeciality);
    }
}