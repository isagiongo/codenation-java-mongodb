package challenge;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/recipe")
public class RecipeController {

	@Autowired
	private RecipeService recipeService;

	@PostMapping
	public Recipe save(@RequestBody Recipe recipe) {
		return recipeService.save(recipe);
	}

	@PutMapping(value = "/{id}")
	public void update(@PathVariable String id, @RequestBody Recipe recipe) {
		recipeService.update(id, recipe);
	}

	@DeleteMapping(value = "/{id}")
	public void delete(@PathVariable String id) {
		recipeService.delete(id);
	}

	@GetMapping(value = "/{id}")
	public Optional<Recipe> get(@PathVariable String id) {
		return recipeService.get(id);
	}

	@GetMapping(value = "/ingredient")
	public List<Recipe> listByIngredient(@RequestParam String ingredient) {
		return recipeService.listByIngredient(ingredient);
	}

	@GetMapping(value = "/search")
	public List<Recipe> search(@RequestParam String search) {
		return recipeService.search(search);
	}

	@PostMapping(value = "/{id}/like/{userId}")
	public void like(@PathVariable String id, @PathVariable String userId) {
		recipeService.like(id, userId);
	}

	@RequestMapping(value="/{id}/like/{userId}",method={RequestMethod.GET,RequestMethod.DELETE})
	public void unlike(@PathVariable String id, @PathVariable String userId) {
		recipeService.unlike(id, userId);
	}

	@PostMapping(value = "/{id}/comment")
	public RecipeComment addComment(@PathVariable String id, @RequestBody RecipeComment comment) {
		return recipeService.addComment(id, comment);
	}

	@PutMapping(value = "/{id}/comment/{commentId}")
	public void updateComment(@PathVariable String id, @PathVariable String commentId, @RequestBody RecipeComment comment) {
		recipeService.updateComment(id, commentId, comment);
	}

	@DeleteMapping(value="/{id}/comment/{commentId}")
	public void deleteComment(@PathVariable String id, @PathVariable String commentId) {
		recipeService.deleteComment(id, commentId);
	}

}
