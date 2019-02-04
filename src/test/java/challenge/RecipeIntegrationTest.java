package challenge;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
public class RecipeIntegrationTest {
	
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
    private MongoTemplate mongoTemplate;

	final RecipeTest r1;
	final RecipeTest r2;
	final RecipeTest r3;
	final RecipeCommentTest c1;
	final RecipeCommentTest c2;
	
	public RecipeIntegrationTest() {
		c1 = new RecipeCommentTest();
		c1.setId("5bc6a737953114503ce9cd7f");
		c1.setComment("Muito gostoso! Demais!");
		
		c2 = new RecipeCommentTest();
		c2.setId("5bc6a750953114503ce9cd80");
		c2.setComment("Top!");
		
		r1 = new RecipeTest();
		r1.setId("5bc698399531146718e31220");
		r1.setTitle("Bolo de chocolate feito com MongoDB em java");
		r1.setDescription("Bolo de chocolate natural caseiro");
		r1.setIngredients(Arrays.asList("ovo", "chocolate", "farinha"));
		r1.setLikes(Arrays.asList("123", "456", "789"));
		r1.setComments(Arrays.asList(c1, c2));

		r2 = new RecipeTest();
		r2.setId("5bc932949531144888cc2bd1");
		r2.setTitle("Torta de chocolate com morango");
		r2.setDescription("Torta MongoDB de chocolate com morango");
		r2.setIngredients(Arrays.asList("chocolate", "morango"));

		r3 = new RecipeTest();
		r3.setId("5bc932af9531144888cc2bd2");
		r3.setTitle("Assado de carne com legumes");
		r3.setDescription("Assado de carne com legumas, ovo e lentilha");
		r3.setIngredients(Arrays.asList("cenoura", "cebola", "ovo", "carne"));
	}

	@Test
	public void save() {
		RecipeTest r = new RecipeTest();
		r.setTitle("Nova receita");
		r.setDescription("Adicionando nova receita");
		r.setIngredients(Arrays.asList("nova", "receita"));
		
		RecipeTest response = restTemplate.postForEntity("/recipe", r, RecipeTest.class).getBody();		
		r.setId(response.getId());		
		assertThat(response).isEqualToComparingFieldByFieldRecursively(r);
		
		RecipeTest saved = mongoTemplate.findById(response.getId(), RecipeTest.class);		
		assertThat(saved).isEqualToComparingFieldByFieldRecursively(r);
		
		assertThat(mongoTemplate.count(new Query(), RecipeTest.class)).isEqualTo(4);
	}

	@Test
	public void update() {
		RecipeTest r = new RecipeTest();
		r.setTitle("Receita atualizada");
		r.setDescription("Atualizando receita");
		r.setIngredients(Arrays.asList("atualizada", "receita"));
		
		restTemplate.put("/recipe/" + r1.getId(), r);
		
		RecipeTest updated = mongoTemplate.findById(r1.getId(), RecipeTest.class);
		
		r.setId(r1.getId());
		r.setLikes(r1.getLikes());
		r.setComments(r1.getComments());
		
		assertThat(updated).isEqualToComparingFieldByFieldRecursively(r);		
		assertThat(mongoTemplate.count(new Query(), RecipeTest.class)).isEqualTo(3);
	}

	@Test
	public void delete() {		
		restTemplate.delete("/recipe/" + r1.getId());		

		RecipeTest deleted = mongoTemplate.findById(r1.getId(), RecipeTest.class);
		
		assertThat(deleted).isNull();
		assertThat(mongoTemplate.count(new Query(), RecipeTest.class)).isEqualTo(2);		
	}

	@Test
	public void get() {		
		RecipeTest response = restTemplate.getForEntity("/recipe/" + r1.getId(), RecipeTest.class).getBody();	
		
		assertThat(response).isEqualToComparingFieldByFieldRecursively(r1);
	}

	@Test
	public void listByIngredient() {		
		List<RecipeTest> response = restTemplate.exchange(
				"/recipe/ingredient?ingredient=ovo",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<RecipeTest>>(){}
			).getBody();
		
		assertThat(response).isNotNull();
		assertThat(response.size()).isEqualTo(2);
		assertThat(response.get(0)).isEqualToComparingFieldByFieldRecursively(r3);
		assertThat(response.get(1)).isEqualToComparingFieldByFieldRecursively(r1);
	}

	@Test
	public void search() {		
		List<RecipeTest> response = restTemplate.exchange(
				"/recipe/search?search=mongo",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<RecipeTest>>(){}
			).getBody();
		
		assertThat(response).isNotNull();
		assertThat(response.size()).isEqualTo(2);
		assertThat(response.get(0)).isEqualToComparingFieldByFieldRecursively(r1);
		assertThat(response.get(1)).isEqualToComparingFieldByFieldRecursively(r2);
	}

	@Test
	public void like() {		
		restTemplate.postForEntity("/recipe/" + r1.getId() + "/like/1122", null, Void.class);		

		RecipeTest liked = mongoTemplate.findById(r1.getId(), RecipeTest.class);

		assertThat(liked.getLikes().size()).isEqualTo(4);
		assertThat(liked.getLikes().get(3)).isEqualTo("1122");	
	}
	
	@Test
	public void unlike() {		
		restTemplate.delete("/recipe/" + r1.getId() + "/like/456");		

		RecipeTest liked = mongoTemplate.findById(r1.getId(), RecipeTest.class);

		assertThat(liked.getLikes().size()).isEqualTo(2);
		assertThat(liked.getLikes().get(0)).isEqualTo("123");	
		assertThat(liked.getLikes().get(1)).isEqualTo("789");	
	}
	
	@Test
	public void addComment() {
		RecipeCommentTest c = new RecipeCommentTest();
		c.setComment("New comment");
		
		RecipeCommentTest response = restTemplate.postForEntity("/recipe/" + r1.getId() + "/comment", c, RecipeCommentTest.class).getBody();
		
		c.setId(response.getId());		
		assertThat(response).isEqualToComparingFieldByFieldRecursively(c);
		
		RecipeTest r = mongoTemplate.findById(r1.getId(), RecipeTest.class);
		
		assertThat(r.getComments().size()).isEqualTo(3);		
		assertThat(r.getComments().get(2)).isEqualToComparingFieldByFieldRecursively(c);		
	}
	
	@Test
	public void updateComment() {
		RecipeCommentTest c = new RecipeCommentTest();
		c.setComment("Update comment");
		
		restTemplate.put("/recipe/" + r1.getId() + "/comment/" + c1.getId(), c);
		
		RecipeTest r = mongoTemplate.findById(r1.getId(), RecipeTest.class);
		
		c.setId(c1.getId());
		
		assertThat(r.getComments().size()).isEqualTo(2);		
		assertThat(r.getComments().get(0)).isEqualToComparingFieldByFieldRecursively(c);		
	}
	
	@Test
	public void deleteComment() {		
		restTemplate.delete("/recipe/" + r1.getId() + "/comment/" + c1.getId());
		
		RecipeTest r = mongoTemplate.findById(r1.getId(), RecipeTest.class);
		
		assertThat(r.getComments().size()).isEqualTo(1);		
		assertThat(r.getComments().get(0)).isEqualToComparingFieldByFieldRecursively(c2);		
	}
	
	@Before
	public void before() throws Exception {
		mongoTemplate.dropCollection(RecipeTest.class);
		mongoTemplate.insertAll(Arrays.asList(r1, r2, r3));
    }

}