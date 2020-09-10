package br.ce.wcaquino.taskbackend.controller;//está no mesmo package da TaskController, mesmo estando em pastas totalmente diferentes!!! É algo na mesma vibe do namespace do PHP

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import br.ce.wcaquino.taskbackend.utils.ValidationException;

public class TaskControllerTest {
	
	@Mock
	private TaskRepo taskRepo;// é necessário fazer este mock porque sem ele dá Null Pointer Exception no teste que faz o caminho feliz (o último), porque o objeto dessa classe é instanciado pelo Sprint Boot. Então para não ter que iniciar o Sprint Boot, ter banco de dados, massa de dados e tal, criamos um mock dessa classe para que o teste rode com sucesso sem dependência externa.
	
	@InjectMocks //injeta o mock criado acima dentro da classe TaskController
	private TaskController controller;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);//aqui é que o Mockito faz a magia dos mocks. Lá em cima só está sendo declarado o que ele deve fazer
	}
	
	@Test
	public void naoDeveSalvarTarefaSemDescricao(){
		Task todo = new Task();
		//todo.setTask("Descricao");
		todo.setDueDate(LocalDate.now());
		try {
			controller.save(todo);
			Assert.fail("Não deveria chegar nesse ponto!");//o esperado é que caia no catch
		} catch (ValidationException e) {
			Assert.assertEquals("Fill the task description", e.getMessage());
		}
	}

	@Test
	public void naoDeveSalvarTaferaSemData() {
		Task todo = new Task();
		todo.setTask("Descricao");
		//todo.setDueDate(LocalDate.now());
		try {
			controller.save(todo);
			Assert.fail("Não deveria chegar nesse ponto!");//o esperado é que caia no catch
		} catch (ValidationException e) {
			Assert.assertEquals("Fill the due date", e.getMessage());
		}
	}
	
	@Test
	public void naoDeveSalvarTarefaComDataPassada() {
		Task todo = new Task();
		todo.setTask("Descricao");
		todo.setDueDate(LocalDate.of(2010, 01, 01));
		try {
			controller.save(todo);
			Assert.fail("Não deveria chegar nesse ponto!");//o esperado é que caia no catch
		} catch (ValidationException e) {
			Assert.assertEquals("Due date must not be in past", e.getMessage());
		}
	}
	
	@Test
	public void deveSalvarTarefaComSucesso() throws ValidationException {
		Task todo = new Task();
		todo.setTask("Descricao");
		todo.setDueDate(LocalDate.now());
		controller.save(todo);
		Mockito.verify(taskRepo).save(todo);//verifica se o taskRepo foi chamado quando foi executado o método save(todo) - da linha de cima
	}
}
