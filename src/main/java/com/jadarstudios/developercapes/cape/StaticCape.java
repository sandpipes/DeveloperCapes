/**
 * DeveloperCapes by Jadar
 * License: MIT License
 * (https://raw.github.com/jadar/DeveloperCapes/master/LICENSE)
 * version 4.0.0.x
 */
package com.jadarstudios.developercapes.cape;

import com.jadarstudios.developercapes.DevCapes;
import com.jadarstudios.developercapes.HDImageBuffer;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;

/**
 * Default Cape implementation
 * 
 * @author jadar
 */
public class StaticCape extends AbstractCape {

    public StaticCape(String name, URL url) {
        this.setName(name);
        this.setURL(url);
    }

    public StaticCape(String name) {
        this(name, null);
    }

    @Override
    public void loadTexture(AbstractClientPlayer player) {
        ResourceLocation location = this.getLocation();
        
        try {
            NetworkPlayerInfo npi = Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getUniqueID());
            Field ptF;
            
	        try {
	        	ptF = NetworkPlayerInfo.class.getDeclaredField("playerTextures");
	        } catch (NoSuchFieldException e) {
	        	ptF = NetworkPlayerInfo.class.getDeclaredField("field_187107_a");
	        }
	        
	        ptF.setAccessible(true);
	        Map<Type, ResourceLocation> playerTextures = (Map<Type, ResourceLocation>) ptF.get(npi);
	        playerTextures.put(Type.CAPE, location);
	        ptF.setAccessible(false);
        } catch(Exception e) {
        	e.printStackTrace();
        	DevCapes.logger.error("Error while putting cape texture location.");
        }
        
        Minecraft.getMinecraft().renderEngine.loadTexture(location, this.getTexture());
    }

    @Override
    public boolean isTextureLoaded(AbstractClientPlayer player) {
        ResourceLocation cape = player.getLocationCape();
        return cape != null;
    }

    public void setURL(URL url) {
        if (url == null) {
            this.texture = null;
            return;
        }
        this.texture = new ThreadDownloadImageData(null, url.toString(), null, new HDImageBuffer());
    }

    public void setName(String name) {
        this.name = name;
        this.location = new ResourceLocation("DevCapes/" + name);
    }
}