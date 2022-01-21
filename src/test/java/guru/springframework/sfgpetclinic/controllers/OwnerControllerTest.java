package guru.springframework.sfgpetclinic.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

	private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

	private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";

	@Mock
	OwnerService service;
	
	@Mock
	Model model;

	@InjectMocks
	OwnerController controller;

	@Mock
	BindingResult result;

	//ArgumentCaptor<>の定義: 1,アノテーション 2, ArgumentCaptor.forClass(引数の型)
	@Captor
	ArgumentCaptor<String> stringArgumentCaptor;

	@BeforeEach
	//全てのテストメソッドのgiven or when(mockの設定)を設定
	void setUp() {
		//willAnswer(invocation ->) :mockMethodの引数に応じて動的に戻り値を設定する場合
		//invocation : mockMethodが呼ばれたときの操作を行える
		given(service.findAllByLastNameLike(stringArgumentCaptor.capture()))
				.willAnswer(invocation -> {
					List<Owner> owners = new ArrayList<Owner>();
					//invocation.getArgument(index) : mockMethodを呼び出したときの引数を取得
					String name = invocation.getArgument(0);

					if (name.equals("%Andrew%")) {
						owners.add(new Owner(1l, "Tom", "Andrew"));
						return owners;
					} else if (name.equals("%DontFindMe%")) {
						return owners;
					} else if (name.equals("%FindMe%")) {
						owners.add(new Owner(1l, "Tom", "Andrew"));
						owners.add(new Owner(2l, "Tom2", "Andrew2"));
						return owners;
					}
					//どれにも当てはまらないい場合は例外を投げて処理を終了
					throw new RuntimeException("Invalid Argument");
				});
	}

	@Test
	void processFindFormWildcardFound() {
		//given
		Owner owner = new Owner(1l, "Tom", "FindMe");
		//inOrder(mock...) : mockを読み込む順番を指定する順番オブジェクトを返す
		InOrder inOrder = inOrder(service,model);
		
		//when
		String viewName = controller.processFindForm(owner, result, model);

		//then
		assertThat("%FindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
		assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);
		
		//order verify
		inOrder.verify(service).findAllByLastNameLike(anyString());
		inOrder.verify(model).addAttribute(anyString(), anyList());
		
		verifyNoMoreInteractions(model);
	}

	@Test
	void processFindFormWildcardNotFound() {
		//given
		Owner owner = new Owner(1l, "Tom", "DontFindMe");

		//when
		String viewName = controller.processFindForm(owner, result, null);
		//then
		assertThat("%DontFindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
		assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
		verifyZeroInteractions(model);
	}

	@Test
	void processFindFormWildcardStringAnnotation() {
		//given
		Owner owner = new Owner(1l, "Tom", "Andrew");
		//when
		String viewName = controller.processFindForm(owner, result, null);
		//then
		assertThat("%Andrew%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
		assertThat("redirect:/owners/1").isEqualToIgnoringCase(viewName);
		verifyZeroInteractions(model);
	}

	@Test
	void processCreationFormHasErrors() {
		//given
		Owner owner = new Owner(1l, "Tom", "Andrew");
		given(result.hasErrors()).willReturn(true);
		//when
		String viewName = controller.processCreationForm(owner, result);
		//then
		assertThat(viewName).isEqualTo(OWNERS_CREATE_OR_UPDATE_OWNER_FORM);
	}

	@Test
	void processCreationFormNoErrors() {
		//given
		Owner owner = new Owner(5l, "Tom", "Andrew");
		given(result.hasErrors()).willReturn(false);
		given(service.save(any())).willReturn(owner);
		//when
		String viewName = controller.processCreationForm(owner, result);
		//then
		assertThat(viewName).isEqualToIgnoringCase(REDIRECT_OWNERS_5);
	}
}
