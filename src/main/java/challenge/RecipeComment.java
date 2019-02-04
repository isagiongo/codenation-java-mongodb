package challenge;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Classe para mapear o coment�rio da receita no MongoDB
 *
 */
@Document
public class RecipeComment {
	
	@Id
	private String id;
	private String comment;
	
	public RecipeComment() {
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getId() {
		return id;
	}

	
}
