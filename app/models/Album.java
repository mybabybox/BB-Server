package models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.Constraints.Required;

import com.google.common.base.Objects;

import domain.SocialObjectType;

/**
 * Represent a folder contains a set of Resources
 * 
 */
@Entity
public class Album extends SocialObject {

	public Album() {
	}

	public Album(String name) {
		this.name = name;
	}

	@OneToOne(cascade=CascadeType.REMOVE)
	public Resource cover_photo;

	@Lob
	public String description;
	
	@Required
	@ManyToOne
	public Folder folder;

	@OneToMany(cascade = CascadeType.REMOVE)
	public List<Resource> resources = new ArrayList<Resource>();

	@Override
	public String toString() {
		return super.toString() + " " + name;
	}

	public Resource addFile(java.io.File source, String description,
			SocialObjectType type) throws IOException {

		Resource resource = new Resource(type);
		resource.resourceName = source.getName();
		resource.description = description;
		resource.folder = this.folder;
		resource.owner = this.owner;
		resource.save();
		FileUtils.copyFile(source, new java.io.File(resource.getPath()));
		if (type == SocialObjectType.PHOTO) {
			Thumbnails
					.of(source)
					.height(100)
					.keepAspectRatio(true)
					.toFiles(
							new java.io.File(resource.getPath())
									.getParentFile(),
							Rename.PREFIX_DOT_THUMBNAIL);
		}
		this.resources.add(resource);
		merge();
		recordAddedPhoto(owner);
		return resource;
	}

	public Resource addFile(java.io.File file, SocialObjectType type)
			throws IOException {
		return addFile(file, description, type);
	}

	

	

	public Resource setCoverPhoto_TOAlbum(java.io.File file) throws IOException {

		cover_photo = this.addFile(file, SocialObjectType.PHOTO);
		cover_photo.save();
		return cover_photo;

	}

	/**
	 * get the photo profile
	 * 
	 * @return the resource, null if not exist
	 */
	@JsonIgnore
	public Resource getCoverPhoto_TOAlbum() {
		if (cover_photo != null) {
			Resource file = this.getHighPriorityFile();
			if (file != null) {
				return file;
			}
		}
		return null;
	}

	public String getPhotoProfileURL() {
		Resource resource = getCoverPhoto_TOAlbum();
		if (resource == null) {
			return "";
		}
		return resource.getPath();
	}

	

	public void removeFile(Resource resource) {
		File file = new File(resource.getPath());
		try {
			FileUtils.cleanDirectory(file.getParentFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.resources.remove(resource);
	}

	public void setHighPriorityFile(Resource high) {
		int max = Integer.MIN_VALUE;
		for (Resource file : resources) {
			if (file.priority > max) {
				max = file.priority;
			}
		}
		high.priority = max + 1;
		high.save();
	}

	public Resource getHighPriorityFile() {
		int max = Integer.MIN_VALUE;
		Resource highest = null;
		for (Resource file : resources) {
			if (file.priority > max) {
				highest = file;
				max = file.priority;
			}
		}
		return highest;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Album) {
			final Album other = (Album) obj;
			return new EqualsBuilder().append(name, other.name).isEquals();
		} else {
			return false;
		}
	}

}
