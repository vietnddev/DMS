package com.flowiee.dms.storage.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.File;
import java.util.List;

@Builder
@Getter
@Setter
public class FolderTree {
    private int level;
    private long id;
    private String name;
    private long parentId;
    private String parentName;
    private boolean isDirectory;
    private File file;
    private List<FolderTree> subFiles;


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("level", level)
                .append("name", name)
                .append("parentName", parentName)
                .append("isDirectory", isDirectory)
                .toString();
    }
}