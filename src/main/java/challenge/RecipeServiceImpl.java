package challenge;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class RecipeServiceImpl implements RecipeService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private RecipeRepository recipeRepository;

	@Override
	public Recipe save(Recipe recipe) {
		return recipeRepository.insert(recipe);
	}

	@Override
	public void update(String id, Recipe recipe) {
		mongoTemplate.updateFirst(
				Query.query(Criteria.where("_id").is(id)), Update.update("title", recipe.getTitle())
						.set("description", recipe.getDescription()).set("ingredients", recipe.getIngredients()),
				Recipe.class);
	}

	@Override
	public void delete(String id) {
		recipeRepository.deleteById(id);
	}

	@Override
	public Optional<Recipe> get(String id) {
		return recipeRepository.findById(id);
	}

	@Override
	public List<Recipe> listByIngredient(String ingredient) {
		return mongoTemplate.find(
				Query.query(Criteria.where("ingredients").is(ingredient)).with(Sort.by("title").ascending()),
				Recipe.class);
	}

	@Override
	public List<Recipe> search(String search) {
		return mongoTemplate.find(Query.query(Criteria.where("title").is(search).and("description").is(search))
				.with(Sort.by("title").ascending()), Recipe.class);
	}

//	@Override
//	public void like(String id, String userId) {
//	}
//
//	@Override
//	public void unlike(String id, String userId) {
//
//	}
//
//	@Override
//	public RecipeComment addComment(String id, RecipeComment comment) {
//		return null;
//	}
//
//	@Override
//	public void updateComment(String id, String commentId, RecipeComment comment) {
//
//	}
//
//	@Override
//	public void deleteComment(String id, String commentId) {
//
//	}

}
