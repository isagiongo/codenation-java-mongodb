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

//	public void like() {
//		service.like(null, null);
//	}
//
//	public void unlike() {
//		service.unlike(null, null);
//	}
//
//	public RecipeComment addComment() {
//		return service.addComment(null, null);
//	}
//
//	public void updateComment() {
//		service.updateComment(null, null, null);
//	}
//
//	public void deleteComment() {
//		service.deleteComment(null, null);
//	}

}
