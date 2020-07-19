package de.cas_ual_ty.ydm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class YDMResourcePack extends ResourcePack
{
    private static final boolean OS_WINDOWS = Util.getOSType() == Util.OS.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');
    
    private FileFilterSuffix filter;
    
    private JsonObject packMeta;
    
    public YDMResourcePack(File folder, FileFilterSuffix filter)
    {
        super(folder);
        this.filter = filter;
        
        this.packMeta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("description", "YDM Card Images");
        pack.addProperty("pack_format", 5);
        this.packMeta.add("pack", pack);
    }
    
    public static boolean validatePath(File fileIn, String pathIn) throws IOException
    {
        String s = fileIn.getCanonicalPath();
        if(YDMResourcePack.OS_WINDOWS)
        {
            s = YDMResourcePack.BACKSLASH_MATCHER.replaceFrom(s, '/');
        }
        
        return s.endsWith(pathIn);
    }
    
    @Override
    protected InputStream getInputStream(String resourcePath) throws IOException
    {
        //TODO pack.png
        System.out.println("getInputStream: " + resourcePath);
        File file1 = this.getFile(resourcePath);
        if(file1 == null)
        {
            throw new ResourcePackFileNotFoundException(this.file, resourcePath);
        }
        else
        {
            return new FileInputStream(file1);
        }
    }
    
    @Override
    protected boolean resourceExists(String resourcePath)
    {
        System.out.println("resourceExists: " + resourcePath);
        return this.getFile(resourcePath + this.filter.getRequiredSuffix()) != null;
    }
    
    @Nullable
    private File getFile(String filename)
    {
        try
        {
            File file1 = new File(this.file, filename + this.filter.getRequiredSuffix());
            if(file1.isFile() && YDMResourcePack.validatePath(file1, filename))
            {
                return file1;
            }
        }
        catch (IOException var3)
        {
            
        }
        
        return null;
    }
    
    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type)
    {
        return type == ResourcePackType.CLIENT_RESOURCES ? ImmutableSet.<String>of(YDM.MOD_ID) : ImmutableSet.<String>of();
    }
    
    @Override
    public void close() throws IOException
    {
    }
    
    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn)
    {
        List<ResourceLocation> list = Lists.newArrayList();
        System.out.println("getAllResourceLocations: " + type + " " + namespaceIn + " " + pathIn);
        
        if(type == ResourcePackType.CLIENT_RESOURCES)
        {
            File[] listFiles = this.file.listFiles(this.filter);
            
            if(listFiles != null)
            {
                for(File f : listFiles)
                {
                    list.add(new ResourceLocation(YDM.MOD_ID, f.getName().replace(this.filter.getRequiredSuffix(), "")));
                }
            }
        }
        
        return list;
    }
    
    @Override
    public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException
    {
        return deserializer.deserialize(JSONUtils.getJsonObject(this.packMeta, deserializer.getSectionName()));
    }
}
