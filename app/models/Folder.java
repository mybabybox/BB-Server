package models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import akka.actor.ActorSystem;
import common.image.ImageDimensions;
import common.utils.ImageFileUtil;
import common.utils.NanoSecondStopWatch;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.Objects;

import domain.Creatable;
import domain.SocialObjectType;
import domain.Updatable;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

/**
 * Represent a folder contains a set of Resources
 * 
 */
@Entity

public class Folder extends SocialObject implements Serializable, Creatable, Updatable{
    private static play.api.Logger logger = play.api.Logger.apply(Folder.class);

	public Folder() {}
	
	public Folder(String name) {
		this.name = name;
	}

	@Lob
	public String description;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "folder")
	public List<Resource> resources = new ArrayList<Resource>();

	@Override
	public String toString() {
		return super.toString() + " " + name;
	}

	public static Long[] getResources(Folder folder) {
		Long[] images = null;
		if (folder != null && !CollectionUtils.isEmpty(folder.resources)) {
		    images = new Long[folder.resources.size()];
			int i = 0;
			for (Resource rs : folder.resources) {
			    images[i++] = rs.id;
			}
		}
		return images;
	}
	
    /**
     * @param owner
     * @param name
     * @param description
     * @param system
     * @return
     */
    public static Folder createFolder(User owner, String name, String description, Boolean system) {
        Folder folder = createFolder(owner, name, description,
                SocialObjectType.FOLDER, system);
        return folder;
    }

    public static Folder createFolder(User owner, String name, String description,
                                      SocialObjectType type, Boolean system) {
        Folder folder = new Folder(name);
        folder.owner = owner;
        folder.name = name;
        folder.description = description;
        folder.objectType = type;
        folder.system = system;
        folder.save();
        return folder;
    }

	/**
	 * Note: for profile pics, scale it up by 100% as browser will only display crispy profile pic 
	 * with higher resolution natural size photo. (seems like cover photo does not have this issue)
	 * 
	 * @param source
	 * @param description
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public Resource addFile(final java.io.File source,
                            final String description,
			                final SocialObjectType type) throws IOException {
        logger.underlyingLogger().info("addFile("+type.name()+") - start.");
        NanoSecondStopWatch fullSw = new NanoSecondStopWatch();

		final Resource resource = new Resource(type);
		resource.resourceName = source.getName();
		resource.description = description;
		resource.folder = this;
		resource.owner = this.owner;
		resource.save();

        final File parentFile = new java.io.File(resource.getPath()).getParentFile();
        if (!parentFile.exists()) {
            boolean mkdirsSuccess = parentFile.mkdirs();
            if (!mkdirsSuccess) {
                logger.underlyingLogger().error("Failed to mkdirs: "+parentFile.getName());
            }
        }

        // Perf: This copies the original sized file!
        //FileUtils.copyFile(source, new java.io.File(resource.getPath()));

		if (type == SocialObjectType.PROFILE_PHOTO) {
            NanoSecondStopWatch sw = new NanoSecondStopWatch();
            String fileName = new java.io.File(resource.getPath()).getName();

		    Thumbnails.of(source)
                    .height(ImageDimensions.PROFILE_FULL)
                    .width(ImageDimensions.PROFILE_FULL)
                    .keepAspectRatio(true)
                    .toFiles(parentFile, Rename.NO_CHANGE);
		    
			Thumbnails.of(source)
					.height(ImageDimensions.PROFILE_THUMBNAIL)
					.width(ImageDimensions.PROFILE_THUMBNAIL)
					.keepAspectRatio(true)
					.toFiles(parentFile, Rename.PREFIX_DOT_THUMBNAIL);
			
			Thumbnails.of(source)
					.height(ImageDimensions.PROFILE_MINI)
					.width(ImageDimensions.PROFILE_MINI)
					.keepAspectRatio(true)
					.toFile(parentFile+"/mini."+fileName);
			
            sw.stop();
            logger.underlyingLogger().info("addFile("+type.name()+"). Resize Took "+sw.getElapsedMS()+"ms");
		} 
		else if (type == SocialObjectType.COVER_PHOTO) {
            NanoSecondStopWatch sw = new NanoSecondStopWatch();
            String fileName = new java.io.File(resource.getPath()).getName();

		    Thumbnails.of(source)
					.width(ImageDimensions.COVERPHOTO_FULL_WIDTH)
					.keepAspectRatio(true)
					.toFiles(parentFile, Rename.NO_CHANGE);

		    Thumbnails.of(source)
                    .width(ImageDimensions.COVERPHOTO_THUMBNAIL_WIDTH)
                    .keepAspectRatio(true)
                    .toFiles(parentFile, Rename.PREFIX_DOT_THUMBNAIL);
			
			Thumbnails.of(source)
					.width(ImageDimensions.COVERPHOTO_MINI_WIDTH)
					.keepAspectRatio(true)
					.toFile(parentFile+"/mini."+fileName);

            sw.stop();
            logger.underlyingLogger().info("addFile("+type.name()+"). Resize Took "+sw.getElapsedMS()+"ms");
		}
		else if (type == SocialObjectType.POST_PHOTO) {
            NanoSecondStopWatch sw = new NanoSecondStopWatch();
            String fileName = new java.io.File(resource.getPath()).getName();

			BufferedImage bimg = ImageFileUtil.readImageFile(source);
			final int origWidth  = bimg.getWidth();
			final int origHeight = bimg.getHeight();

            // horizontal or square
			if(origWidth >= origHeight) {
                int targetPreviewWidth = ImageDimensions.POST_IMAGE_PREVIEW_WIDTH_PX;
                if (origWidth > targetPreviewWidth) {
                    double scaleFactor = ((double)targetPreviewWidth) / ((double)origWidth);
                    Thumbnails.of(source)
                    			.scale(scaleFactor)
                                .toFiles(parentFile, Rename.PREFIX_DOT_THUMBNAIL);
                } else {
                    FileUtils.copyFile(source, new java.io.File(resource.getThumbnail()));
                }

                ActorSystem actorSystem = Akka.system();
                actorSystem.scheduler().scheduleOnce(
                    Duration.create(0, TimeUnit.MILLISECONDS),
                    new Runnable() {
                        public void run() {
                            try {
                                int targetFullWidth = ImageDimensions.LIGHTBOX_WIDTH_PX;
                                if (origWidth > targetFullWidth) {
                                    Thumbnails.of(source)
                                            .width(targetFullWidth)
                                            .keepAspectRatio(true)
                                            .toFiles(parentFile, Rename.NO_CHANGE);
                                } else {
                                    FileUtils.copyFile(source, new java.io.File(resource.getPath()));
                                }
                            } catch (Exception e) {
                                logger.underlyingLogger().error("Failed to resize", e);
                            }
                        }
                    }, actorSystem.dispatcher()
                );
			}
            // vertical pictures
            else {
                int targetPreviewHeight = ImageDimensions.POST_IMAGE_PREVIEW_HEIGHT_PX;
                if (origHeight > targetPreviewHeight) {
                    double scaleFactor = ((double)targetPreviewHeight) / ((double)origHeight);
                    Thumbnails.of(source)
                                .scale(scaleFactor)
                                .toFiles(parentFile, Rename.PREFIX_DOT_THUMBNAIL);
                } else {
                    FileUtils.copyFile(source, new java.io.File(resource.getThumbnail()));
                }

                ActorSystem actorSystem = Akka.system();
                actorSystem.scheduler().scheduleOnce(
                    Duration.create(0, TimeUnit.MILLISECONDS),
                    new Runnable() {
                        public void run() {
                            try {
                                int targetFullHeight = ImageDimensions.LIGHTBOX_HEIGHT_PX;
                                if (origHeight > targetFullHeight) {
                                    Thumbnails.of(source)
                                            .height(targetFullHeight)
                                            .keepAspectRatio(true)
                                            .toFiles(parentFile, Rename.NO_CHANGE);
                                } else {
                                    FileUtils.copyFile(source, new java.io.File(resource.getPath()));
                                }
                            } catch (Exception e) {
                                logger.underlyingLogger().error("Failed to resize", e);
                            }
                        }
                    }, actorSystem.dispatcher()
                );
			}

			// mini
            Thumbnails.of(source)
            			.height(ImageDimensions.PROFILE_MINI)
            			.width(ImageDimensions.PROFILE_MINI)
            			.keepAspectRatio(true)
            			.toFile(parentFile+"/mini."+fileName);
            
            sw.stop();
            logger.underlyingLogger().info("addFile("+type.name()+"). Resize Took "+sw.getElapsedMS()+"ms");
		}
		else if (type == SocialObjectType.MESSAGE_PHOTO) {
            NanoSecondStopWatch sw = new NanoSecondStopWatch();

            BufferedImage bimg = ImageFileUtil.readImageFile(source);
			final int origWidth  = bimg.getWidth();
			final int origHeight = bimg.getHeight();

			if (origWidth >= origHeight) {
                // horizontal or square
                int targetPreviewWidth = ImageDimensions.PM_IMAGE_PREVIEW_WIDTH_PX;
                if (origWidth > targetPreviewWidth) {
                    Thumbnails.of(source)
                            .width(targetPreviewWidth)
                            .keepAspectRatio(true)
                            .toFiles(parentFile, Rename.PREFIX_DOT_THUMBNAIL);
                } else {
                    FileUtils.copyFile(source, new java.io.File(resource.getThumbnail()));
                }

                ActorSystem actorSystem = Akka.system();
                actorSystem.scheduler().scheduleOnce(
                    Duration.create(0, TimeUnit.MILLISECONDS),
                    new Runnable() {
                        public void run() {
                            try {
                                int targetFullWidth = ImageDimensions.LIGHTBOX_WIDTH_PX;
                                if (origWidth > targetFullWidth) {
                                    Thumbnails.of(source)
                                            .width(targetFullWidth)
                                            .keepAspectRatio(true)
                                            .toFiles(parentFile, Rename.NO_CHANGE);
                                } else {
                                    FileUtils.copyFile(source, new java.io.File(resource.getPath()));
                                }
                            } catch (Exception e) {
                                logger.underlyingLogger().error("Failed to resize", e);
                            }
                        }
                    }, actorSystem.dispatcher()
                );
			} else {
                int targetPreviewHeight = ImageDimensions.PM_IMAGE_PREVIEW_HEIGHT_PX;
                if (origHeight > targetPreviewHeight) {
                    Thumbnails.of(source)
                            .height(targetPreviewHeight)
                            .keepAspectRatio(true)
                            .toFiles(parentFile, Rename.PREFIX_DOT_THUMBNAIL);
                } else {
                    FileUtils.copyFile(source, new java.io.File(resource.getThumbnail()));
                }

                ActorSystem actorSystem = Akka.system();
                actorSystem.scheduler().scheduleOnce(
                    Duration.create(0, TimeUnit.MILLISECONDS),
                    new Runnable() {
                        public void run() {
                            try {
                                int targetFullHeight = ImageDimensions.LIGHTBOX_HEIGHT_PX;
                                if (origHeight > targetFullHeight) {
                                    Thumbnails.of(source)
                                            .height(targetFullHeight)
                                            .keepAspectRatio(true)
                                            .toFiles(parentFile, Rename.NO_CHANGE);
                                } else {
                                    FileUtils.copyFile(source, new java.io.File(resource.getPath()));
                                }
                            } catch (Exception e) {
                                logger.underlyingLogger().error("Failed to resize", e);
                            }
                        }
                    }, actorSystem.dispatcher()
                );
			}

            sw.stop();
            logger.underlyingLogger().info("addFile("+type.name()+"). Resize Took "+sw.getElapsedMS()+"ms");
		}

		this.resources.add(resource);
		merge();

        fullSw.stop();
        logger.underlyingLogger().info("addFile("+type.name()+") - end. Total Took "+fullSw.getElapsedMS()+"ms");

		return resource;
	}

	public Resource addFile(java.io.File file, SocialObjectType type)
			throws IOException {
		return addFile(file, description, type);
	}

	public Resource addExternalFile(URL url, String description,
			SocialObjectType type) throws IOException {
		Resource file = new Resource(type);
		file.resourceName = url.toString();
		file.description = description;
		file.folder = this;
		file.owner = this.owner;
		this.resources.add(file);
		return file;
	}

	public Resource addExternalFile(URL url, SocialObjectType type)
			throws IOException {
		return addExternalFile(url, null, type);
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
		if (obj instanceof SocialObject) {
			final SocialObject other = (SocialObject) obj;
			return new EqualsBuilder().append(name, other.name).isEquals();
		} else {
			return false;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

}
