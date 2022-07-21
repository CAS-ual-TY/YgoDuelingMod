package de.cas_ual_ty.ydm.clientutil;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class YdmCardResourcePack extends ResourcePack
{
    public static final String PATH_PREFIX = "assets/" + YDM.MOD_ID + "/textures/item/";
    
    private static final boolean OS_WINDOWS = Util.getPlatform() == Util.OS.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');
    
    private JsonObject packMeta;
    
    public YdmCardResourcePack()
    {
        super(null);
        packMeta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("description", "All dynamically downloaded images of the Ygo Dueling Mod.");
        pack.addProperty("pack_format", 6);
        packMeta.add("pack", pack);
    }
    
    public static String convertPath(String s)
    {
        if(YdmCardResourcePack.OS_WINDOWS)
        {
            s = YdmCardResourcePack.BACKSLASH_MATCHER.replaceFrom(s, '/');
        }
        
        return s;
    }
    
    @Override
    protected InputStream getResource(String resourcePath) throws IOException
    {
        //TODO pack.png needs to be returned as well
        
        // We get system dependent resource paths here (so eg. \ for windows, / for mac) so we need to convert
        File image = getFile(YdmCardResourcePack.convertPath(resourcePath));
        
        if(image == null)
        {
            throw new ResourcePackFileNotFoundException(ClientProxy.cardImagesFolder, resourcePath);
        }
        else
        {
            return new FileInputStream(image);
        }
    }
    
    @Override
    protected boolean hasResource(String resourcePath)
    {
        return getFile(resourcePath) != null;
    }
    
    @Nullable
    private File getFile(String filename)
    {
        if(!filename.endsWith(".png"))
        {
            return null;
        }
        
        // We only look for assets with this path as prefix (so eg. no models)
        if(!filename.startsWith(YdmCardResourcePack.PATH_PREFIX))
        {
            return null;
        }
        
        // We remove that prefix part
        filename = filename.substring(YdmCardResourcePack.PATH_PREFIX.length());
        
        // Get the file
        File image = ImageHandler.getCardFile(filename);
        
        if(image.exists())
        {
            return image;
        }
        else
        {
            image = ImageHandler.getSetFile(filename);
            
            if(image.exists())
            {
                return image;
            }
            else
            {
                return null;
            }
        }
    }
    
    @Override
    public Set<String> getNamespaces(ResourcePackType type)
    {
        return type == ResourcePackType.CLIENT_RESOURCES ? ImmutableSet.of(YDM.MOD_ID) : ImmutableSet.of();
    }
    
    @Override
    public void close()
    {
    }
    
    @Override
    public Collection<ResourceLocation> getResources(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn)
    {
        // This is only needed for fonts and sounds afaik
        /*
        List<ResourceLocation> list = Lists.newArrayList();
        
        if(type == ResourcePackType.CLIENT_RESOURCES)
        {
            File[] listFiles = ClientProxy.cardImagesFolder.listFiles(this.filter);
            
            if(listFiles != null)
            {
                for(File f : listFiles)
                {
                    list.add(new ResourceLocation(YDM.MOD_ID, f.getName().replace(this.filter.getRequiredSuffix(), "")));
                }
            }
        }
        */
        return Collections.emptyList();
    }
    
    @Override
    public <T> T getMetadataSection(IMetadataSectionSerializer<T> deserializer) throws IOException
    {
        if(deserializer.getMetadataSectionName().equals("pack"))
        {
            return deserializer.fromJson(JSONUtils.getAsJsonObject(packMeta, deserializer.getMetadataSectionName()));
        }
        return null;
    }
    
    @Override
    public String getName()
    {
        return "YDM Images";
    }
}
