package de.cas_ual_ty.ydm;

import com.google.common.io.Files;
import com.google.gson.*;
import de.cas_ual_ty.ydm.card.CustomCards;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.rarity.RarityEntry;
import de.cas_ual_ty.ydm.set.CardSet;
import de.cas_ual_ty.ydm.set.Distribution;
import de.cas_ual_ty.ydm.util.DNCList;
import de.cas_ual_ty.ydm.util.JsonKeys;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class YdmDatabase
{
    public static final DNCList<Long, Properties> PROPERTIES_LIST = new DNCList<>((p) -> p.getId(), Long::compare);
    private static int cardsVariantsCount = -1;
    
    public static final HashSet<String> FOUND_RARITIES = new HashSet<>();
    
    public static final DNCList<String, RarityEntry> RARITIES_LIST = new DNCList<>((r) -> r.rarity, (s1, s2) -> s1.compareTo(s2));
    public static final DNCList<String, Distribution> DISTRIBUTIONS_LIST = new DNCList<>((d) -> d.name, (s1, s2) -> s1.compareTo(s2));
    public static final DNCList<String, CardSet> SETS_LIST = new DNCList<>((s) -> s.code, (s1, s2) -> s1.compareTo(s2));
    
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final SimpleDateFormat SET_DATE_PARSER = new SimpleDateFormat("dd-MM-yyyy");
    
    public static boolean databaseReady = false;
    
    public static JsonObject localDbInfo = null;
    public static int localVersionIteration = Integer.MIN_VALUE;
    public static String localVersionId = null;
    
    public static JsonObject remoteDbInfo = null;
    public static int remoteVersionIteration = Integer.MIN_VALUE;
    public static String remoteDownloadLink = null;
    public static String remoteVersionId = null;
    
    public static void initDatabase()
    {
        if(!YDM.dbSourceUrl.isEmpty())
        {
            boolean downloadDB = false;
            boolean remoteRead = false;
            
            if(!YDM.mainFolder.exists())
            {
                downloadDB = true;
            }
            else
            {
                try
                {
                    if(YdmDatabase.readLocalVersion())
                    {
                        remoteRead = true;
                        if(YdmDatabase.readRemoteVersion() && (YdmDatabase.localVersionIteration < YdmDatabase.remoteVersionIteration || !YdmDatabase.localVersionId.equals(YdmDatabase.remoteVersionId)))
                        {
                            YDM.log("New database version: " + YdmDatabase.remoteVersionIteration + " (Old version: " + YdmDatabase.localVersionIteration + ")");
                            downloadDB = true;
                        }
                    }
                    else
                    {
                        downloadDB = true;
                    }
                }
                catch(Exception e)
                {
                    downloadDB = true;
                    YDM.log("Failed assessing if a new database needs to be downloaded. Doing it anyways...");
                    e.printStackTrace();
                }
            }
            
            if(downloadDB)
            {
                if(!remoteRead)
                {
                    YdmDatabase.readRemoteVersion();
                }
                
                if(YdmDatabase.remoteDownloadLink == null)
                {
                    YDM.log("Cannot download database.");
                    return;
                }
                
                try
                {
                    YdmDatabase.downloadDatabase();
                }
                catch(IOException e)
                {
                    YDM.log("Failed downloading database.");
                    e.printStackTrace();
                    return;
                }
            }
        }
        
        YdmDatabase.readFiles();
    }
    
    private static boolean readLocalVersion()
    {
        try
        {
            File version = new File(YDM.mainFolder, "db.json");
            if(!version.exists())
            {
                YDM.log("Local db.json file does not exist: " + version.getAbsolutePath());
                return false;
            }
            
            YDM.log("Reading local db.json file: " + version.getAbsolutePath());
            YdmDatabase.localDbInfo = YdmIOUtil.parseJsonFile(version).getAsJsonObject();
            YdmDatabase.localVersionIteration = YdmDatabase.localDbInfo.get(JsonKeys.VERSION_ITERATION).getAsInt();
            YdmDatabase.localVersionId = YdmDatabase.localDbInfo.get(JsonKeys.DB_ID).getAsString();
            
            return true;
        }
        catch(IOException e)
        {
            YDM.log("Cannot read local db.json file. Redownloading database...");
            e.printStackTrace();
        }
        catch(JsonParseException e)
        {
            YDM.log("Cannot parse local db.json file. Redownloading database...");
            e.printStackTrace();
        }
        
        return false;
    }
    
    private static boolean readRemoteVersion()
    {
        try
        {
            YDM.log("Reading remote db.json file: " + YDM.dbSourceUrl);
            URL url = new URL(YDM.dbSourceUrl);
            try(InputStream in = YdmIOUtil.urlInputStream(url))
            {
                YdmDatabase.remoteDbInfo = YdmIOUtil.parseJsonFile(new InputStreamReader(in)).getAsJsonObject();
                YdmDatabase.remoteVersionIteration = YdmDatabase.remoteDbInfo.get(JsonKeys.VERSION_ITERATION).getAsInt();
                YdmDatabase.remoteDownloadLink = YdmDatabase.remoteDbInfo.get(JsonKeys.DOWNLOAD_LINK).getAsString();
                YdmDatabase.remoteVersionId = YdmDatabase.remoteDbInfo.get(JsonKeys.DB_ID).getAsString();
            }
            
            return true;
        }
        catch(IOException e)
        {
            YDM.log("Cannot read remote db.json file. Staying on current database...");
            e.printStackTrace();
        }
        catch(JsonParseException | NullPointerException e)
        {
            YDM.log("Cannot parse remote db.json file. Staying on current database...");
            e.printStackTrace();
        }
        
        return false;
    }
    
    private static void readFiles()
    {
        YDM.log("Reading database!");
        YdmDatabase.databaseReady = true;
        
        YdmDatabase.PROPERTIES_LIST.add(Properties.DUMMY);
        YdmDatabase.SETS_LIST.add(CardSet.DUMMY);
        
        CustomCards.createAndRegisterEverything();
        
        if(!YDM.mainFolder.exists())
        {
            YDM.log(YDM.mainFolder.getAbsolutePath() + " (main folder) does not exist! Aborting...");
            return;
        }
        
        if(!YDM.cardsFolder.exists())
        {
            YDM.log(YDM.cardsFolder.getAbsolutePath() + " (cards folder) does not exist! Aborting...");
            return;
        }
        
        YdmDatabase.readCards(YDM.cardsFolder);
        
        if(YDM.distributionsFolder.exists())
        {
            YdmDatabase.readDistributions(YDM.distributionsFolder);
        }
        else
        {
            YDM.log(YDM.distributionsFolder.getAbsolutePath() + " (distributions folder) does not exist! Skipping...");
        }
        
        if(YDM.setsFolder.exists())
        {
            YdmDatabase.readSets(YDM.setsFolder);
        }
        else
        {
            YDM.log(YDM.setsFolder.getAbsolutePath() + " (sets folder) does not exist! Skipping...");
        }
        
        if(YDM.raritiesFolder.exists())
        {
            YdmDatabase.readRarities(YDM.raritiesFolder);
        }
        else
        {
            YDM.log(YDM.raritiesFolder.getAbsolutePath() + " (rarities folder) does not exist! Skipping...");
        }
        
        YdmDatabase.postDBInit();
    }
    
    public static void downloadDatabase() throws IOException
    {
        YDM.log("Downloading database from " + YdmDatabase.remoteDownloadLink);
        
        // remove main folder and contents
        if(YDM.mainFolder.exists())
        {
            YdmIOUtil.deleteRecursively(YDM.mainFolder);
        }
        
        URL url = new URL(YdmDatabase.remoteDownloadLink);
        
        // archive containing the files
        File zip = new File("ydm_db_temp.zip");
        if(zip.exists())
        {
            zip.delete();
        }
        
        // archive to inpack to
        File temp = new File("ydm_db_temp");
        if(temp.exists())
        {
            temp.delete();
        }
        temp.mkdir();
        
        // download the zipped db
        YdmIOUtil.downloadFile(url, zip);
        
        // --- zip unpack ---
        
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zip));
        ZipEntry entry = zipIn.getNextEntry();
        
        byte[] buffer = new byte[1024];
        
        File currentFile;
        FileOutputStream zipOut;
        int length;
        
        while(entry != null)
        {
            currentFile = new File(temp, entry.getName());
            
            if(entry.isDirectory())
            {
                currentFile.mkdir();
            }
            else
            {
                zipOut = new FileOutputStream(currentFile);
                
                while((length = zipIn.read(buffer)) > 0)
                {
                    zipOut.write(buffer, 0, length);
                }
                
                zipOut.close();
            }
            
            entry = zipIn.getNextEntry();
        }
        
        zipIn.closeEntry();
        zipIn.close();
        
        zip.delete();
        
        // --- zip unpack end ---
        
        // now move the file out
        YdmIOUtil.doForDeepSearched(temp, (file) -> file.getName().equals(YDM.mainFolder.getName()), (file) ->
        {
            try
            {
                Files.move(file, YDM.mainFolder);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        });
        
        // now delete temp folder
        YdmIOUtil.deleteRecursively(temp);
        
        //finally recreate the db.json file
        File dbJson = new File(YDM.mainFolder, "db.json");
        
        if(dbJson.exists())
        {
            dbJson.delete();
        }
        dbJson.createNewFile();
        
        try(FileWriter fw = new FileWriter(dbJson))
        {
            YdmIOUtil.GSON.toJson(YdmDatabase.remoteDbInfo, fw);
            fw.flush();
        }
        
        YDM.log("Finished downloading database!");
    }
    
    private static void readCards(File cardsFolder)
    {
        YDM.log("Reading card files from: " + cardsFolder.getAbsolutePath());
        
        File[] cardsFiles = cardsFolder.listFiles(YdmIOUtil.JSON_FILTER);
        YdmDatabase.PROPERTIES_LIST.ensureExtraCapacity(cardsFiles.length);
        
        JsonObject j;
        Properties p;
        
        for(File cardFile : cardsFiles)
        {
            try
            {
                j = YdmIOUtil.parseJsonFile(cardFile).getAsJsonObject();
                p = YdmUtil.buildProperties(j);
                p.addInformation(new LinkedList<>()); // this throws in case of wrong information
                YdmDatabase.PROPERTIES_LIST.add(p);
            }
            catch(NullPointerException | IllegalArgumentException | IllegalStateException e)
            {
                YDM.log("Failed reading card: " + cardFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(JsonSyntaxException e)
            {
                YDM.log("Failed reading card: " + cardFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(JsonIOException | FileNotFoundException e)
            {
                YDM.log("Failed reading card: " + cardFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(IOException e)
            {
                YDM.log("Failed reading card: " + cardFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(Exception e)
            {
                YDM.log("Failed reading card: " + cardFile.getAbsolutePath());
                throw e;
            }
        }
        
        YdmDatabase.PROPERTIES_LIST.sort();
        
        YDM.log("Done reading card files!");
    }
    
    private static void readDistributions(File distributionsFolder)
    {
        YDM.log("Reading distribution files from: " + distributionsFolder.getAbsolutePath());
        
        File[] distributionsFiles = distributionsFolder.listFiles(YdmIOUtil.JSON_FILTER);
        YdmDatabase.DISTRIBUTIONS_LIST.ensureExtraCapacity(distributionsFiles.length);
        
        JsonObject j;
        Distribution d;
        
        for(File distributionFile : distributionsFiles)
        {
            try
            {
                j = YdmIOUtil.parseJsonFile(distributionFile).getAsJsonObject();
                d = new Distribution(j);
                YdmDatabase.DISTRIBUTIONS_LIST.add(d);
            }
            catch(NullPointerException | IllegalArgumentException | IllegalStateException e)
            {
                YDM.log("Failed reading distribution: " + distributionFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(JsonSyntaxException e)
            {
                YDM.log("Failed reading distribution: " + distributionFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(JsonIOException | FileNotFoundException e)
            {
                YDM.log("Failed reading distribution: " + distributionFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(IOException e)
            {
                YDM.log("Failed reading distribution: " + distributionFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(Exception e)
            {
                YDM.log("Failed reading distribution: " + distributionFile.getAbsolutePath());
                throw e;
            }
        }
        
        YdmDatabase.DISTRIBUTIONS_LIST.sort();
        
        YDM.log("Done reading distribution files!");
    }
    
    private static void readRarities(File raritiesFolder)
    {
        YDM.log("Reading rarity files from: " + raritiesFolder.getAbsolutePath());
        
        File[] raritiesFiles = raritiesFolder.listFiles(YdmIOUtil.JSON_FILTER);
        YdmDatabase.RARITIES_LIST.ensureExtraCapacity(raritiesFiles.length);
        
        JsonObject j;
        RarityEntry r;
        
        for(File rarityFile : raritiesFiles)
        {
            try
            {
                j = YdmIOUtil.parseJsonFile(rarityFile).getAsJsonObject();
                r = new RarityEntry(j);
                YdmDatabase.RARITIES_LIST.add(r);
            }
            catch(NullPointerException | IllegalArgumentException | IllegalStateException e)
            {
                YDM.log("Failed reading rarity: " + rarityFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(JsonSyntaxException e)
            {
                YDM.log("Failed reading rarity: " + rarityFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(JsonIOException | FileNotFoundException e)
            {
                YDM.log("Failed reading rarity: " + rarityFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(IOException e)
            {
                YDM.log("Failed reading rarity: " + rarityFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(Exception e)
            {
                YDM.log("Failed reading rarity: " + rarityFile.getAbsolutePath());
                throw e;
            }
        }
        
        YdmDatabase.RARITIES_LIST.sort();
        
        YDM.log("Done reading rarity files!");
    }
    
    private static void readSets(File setsFolder)
    {
        YDM.log("Reading set files from: " + setsFolder.getAbsolutePath());
        
        File[] setsFiles = setsFolder.listFiles(YdmIOUtil.JSON_FILTER);
        YdmDatabase.SETS_LIST.ensureExtraCapacity(setsFiles.length);
        
        JsonObject j;
        CardSet s;
        
        for(File setFile : setsFiles)
        {
            try
            {
                j = YdmIOUtil.parseJsonFile(setFile).getAsJsonObject();
                s = new CardSet(j);
                YdmDatabase.SETS_LIST.add(s);
            }
            catch(NullPointerException | IllegalArgumentException | IllegalStateException e)
            {
                YDM.log("Failed reading set: " + setFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(JsonSyntaxException e)
            {
                YDM.log("Failed reading set: " + setFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(JsonIOException | FileNotFoundException e)
            {
                YDM.log("Failed reading set: " + setFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(IOException e)
            {
                YDM.log("Failed reading set: " + setFile.getAbsolutePath());
                e.printStackTrace();
            }
            catch(Exception e)
            {
                YDM.log("Failed reading set: " + setFile.getAbsolutePath());
                throw e;
            }
        }
        
        YdmDatabase.SETS_LIST.sort();
        
        YDM.log("Done reading set files!");
    }
    
    private static void postDBInit()
    {
        YDM.log("Finalizing database!");
        
        for(Properties x : YdmDatabase.PROPERTIES_LIST)
        {
            x.postDBInit();
        }
        
        for(Distribution x : YdmDatabase.DISTRIBUTIONS_LIST)
        {
            x.postDBInit();
        }
        
        for(CardSet x : YdmDatabase.SETS_LIST)
        {
            x.postDBInit();
        }
        
        SETS_LIST.getList().stream().filter(Objects::nonNull).map(s -> s.rarityPool).filter(Objects::nonNull).forEach(FOUND_RARITIES::addAll);
        
        YDM.log("All rarities found:");
        YDM.log(FOUND_RARITIES.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")));
    }
    
    public static int getTotalCardsAndVariants()
    {
        if(YdmDatabase.cardsVariantsCount == -1)
        {
            YdmDatabase.cardsVariantsCount = YdmDatabase.PROPERTIES_LIST.getList().stream().mapToInt((p) -> p.getImageIndicesAmt()).sum();
        }
        
        return YdmDatabase.cardsVariantsCount;
    }
    
    public static void forAllCardVariants(BiConsumer<Properties, Byte> cardImageConsumer)
    {
        byte i;
        for(Properties c : YdmDatabase.PROPERTIES_LIST)
        {
            if(c == Properties.DUMMY)
            {
                continue;
            }
            
            for(i = 0; i < c.getImageIndicesAmt(); ++i)
            {
                cardImageConsumer.accept(c, i);
            }
        }
    }
    
    public static RarityEntry getRarity(String rarity)
    {
        return rarity != null ? RARITIES_LIST.get(rarity) : null;
    }
}
