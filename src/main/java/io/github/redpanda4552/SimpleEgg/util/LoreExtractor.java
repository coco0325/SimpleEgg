package io.github.redpanda4552.SimpleEgg.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Evoker.Spell;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

@SuppressWarnings("deprecation")
public class LoreExtractor {

    private HashMap<String, String> attributeMap;
    
    /**
     * Assembles an ArrayList of the properties for the specified Entity that
     * is to be used for a spawn egg. All instanceof checks are done internally
     * by the LorePackager, so no type checking is required prior to calling
     * this method. Null Entities will throw an IllegalArgumentException.
     * @param livingEntity - The Entity to assemble a lore for.
     * @return An ArrayList of Strings
     * @throws IllegalArgumentException If entity parameter is null
     */
    public LoreExtractor(ArrayList<String> lore, LivingEntity livingEntity) throws IllegalArgumentException {
        if (lore == null || lore.isEmpty()) {
            throw new IllegalArgumentException("Can't apply null/empty lore!");
        }
        
        if (livingEntity == null) {
            throw new IllegalArgumentException("Can't apply lore to a null entity!");
        }
        
        attributeMap = new HashMap<String, String>();
        
        for (String str : lore) {
            String[] strArr = str.split(": ");
            attributeMap.put(strArr[0], strArr[1]);
        }
        
        livingEntity(livingEntity);
        
        if (livingEntity instanceof Ageable) {
            ageable((Ageable) livingEntity);
            
            if (livingEntity instanceof Sheep) {
                sheep((Sheep) livingEntity);
            } else if (livingEntity instanceof Pig) {
                pig((Pig) livingEntity);
            } else if (livingEntity instanceof Rabbit) {
                rabbit((Rabbit) livingEntity);
            } else if (livingEntity instanceof Villager) {
                villager((Villager) livingEntity);
            } else if (livingEntity instanceof Tameable) {
                tameable((Tameable) livingEntity);
                
                if (livingEntity instanceof AbstractHorse) {
                    abstractHorse((AbstractHorse) livingEntity);
                    
                    if (livingEntity instanceof Horse) {
                        horse((Horse) livingEntity);
                    } else if (livingEntity instanceof ChestedHorse) {
                        chestedHorse((ChestedHorse) livingEntity);
                        
                        if (livingEntity instanceof Llama) {
                            llama((Llama) livingEntity);
                        }
                    }
                } else if (livingEntity instanceof Wolf) {
                    wolf((Wolf) livingEntity);
                } else if (livingEntity instanceof Ocelot) {
                    ocelot((Ocelot) livingEntity);
                }
            }
        } else if (livingEntity instanceof Slime) {
            slime((Slime) livingEntity);
        } else if (livingEntity instanceof Creeper) {
            creeper((Creeper) livingEntity);
        } else if (livingEntity instanceof Zombie) {
            zombie((Zombie) livingEntity);
            
            if (livingEntity instanceof PigZombie) {
                pigZombie((PigZombie) livingEntity);
            } else if (livingEntity instanceof ZombieVillager) {
                zombieVillager((ZombieVillager) livingEntity);
            }
        } else if (livingEntity instanceof Evoker) {
            evoker((Evoker) livingEntity);
        } else if (livingEntity instanceof IronGolem) {
            ironGolem((IronGolem) livingEntity);
        } else if (livingEntity instanceof Snowman) {
            snowman((Snowman) livingEntity);
        }
    }
    
    // Entity specific methods
    private void livingEntity(LivingEntity livingEntity) {
        String[] healthStrings = attributeMap.get("Health").split("/");
        livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Double.parseDouble(healthStrings[1]));
        livingEntity.setHealth(Double.parseDouble(healthStrings[0]));
        double speed = Double.parseDouble(attributeMap.get("Speed"));
        livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
    }
    
    private void ageable(Ageable ageable) {
        ageable.setAge(Integer.parseInt(attributeMap.get("Age (Ticks)")));
    }
    
    private void sheep(Sheep sheep) {
        sheep.setColor(DyeColor.valueOf(attributeMap.get("Color")));
    }
    
    private void pig(Pig pig) {
        pig.setSaddle(Boolean.parseBoolean(attributeMap.get("Saddle")));
    }
    
    private void rabbit(Rabbit rabbit) {
        rabbit.setRabbitType(Rabbit.Type.valueOf(attributeMap.get("Type")));
    }
    
    private void villager(Villager villager) {
        villager.setProfession(Profession.valueOf(attributeMap.get("Profession")));
        villager.setRiches(Integer.parseInt(attributeMap.get("Riches")));
        ArrayList<MerchantRecipe> merchantRecipes = new ArrayList<MerchantRecipe>();
        
        for (int i = 1; i < attributeMap.size(); i++) {
            if (attributeMap.containsKey("Recipe" + i)) {
                merchantRecipes.add(MerchantRecipeConverter.fromString(attributeMap.get("Recipe" + i)));
            } else {
                break;
            }
        }
        
        villager.setRecipes(merchantRecipes);
    }
    
    private void tameable(Tameable tameable) {
        if (!attributeMap.get("Owner").equals("None")) {
            tameable.setOwner(Bukkit.getPlayer(UUID.fromString(attributeMap.get("Owner"))));
            tameable.setTamed(true);
        }
    }
    
    private void abstractHorse(AbstractHorse abstractHorse) {
        abstractHorse.setJumpStrength(Double.parseDouble(attributeMap.get("Jump Power")));
    }
    
    private void horse(Horse horse) {
        if (attributeMap.get("Armor").equals("Iron")) {
            horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING, 1));
        } else if (attributeMap.get("Armor").equals("Gold")) {
            horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING, 1));
        } else if (attributeMap.get("Armor").equals("Diamond")) {
            horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING, 1));
        }
        
        if (attributeMap.get("Saddle").equals("Yes")) {
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        }
        
        horse.setColor(Horse.Color.valueOf(attributeMap.get("Color")));
        horse.setStyle(Horse.Style.valueOf(attributeMap.get("Style")));
    }
    
    private void chestedHorse(ChestedHorse chestedHorse) {
        if (Boolean.parseBoolean(attributeMap.get("Carrying Chest"))) {
            chestedHorse.setCarryingChest(true);
        } else {
            chestedHorse.setCarryingChest(false);
        }
    }
    
    private void llama(Llama llama) {
        llama.setColor(Color.valueOf(attributeMap.get("Color")));
        llama.setStrength(Integer.parseInt(attributeMap.get("Strength")));
        
        if (attributeMap.containsKey("Decor")) {
            llama.getInventory().setDecor(ItemStackConverter.fromString(attributeMap.get("Decor")));
        }
    }
    
    private void wolf(Wolf wolf) {
        if (attributeMap.get("Angry").equals("Yes")) {
            wolf.setAngry(true);
        } else {
            wolf.setAngry(false);
        }
        
        if (attributeMap.containsKey("Collar")) {
            wolf.setCollarColor(DyeColor.valueOf(attributeMap.get("Collar")));
        }
    }
    
    private void ocelot(Ocelot ocelot) {
        ocelot.setCatType(Ocelot.Type.valueOf(attributeMap.get("Type")));
        ocelot.setSitting(Boolean.parseBoolean(attributeMap.get("Sitting")));
    }
    
    private void slime(Slime slime) {
        slime.setSize(Integer.parseInt(attributeMap.get("Size")));
    }
    
    private void creeper(Creeper creeper) {
        if (attributeMap.get("Charged").equals("Yes")) {
            creeper.setPowered(true);
        } else {
            creeper.setPowered(false);
        }
    }
    
    private void zombie(Zombie zombie) {
        if (attributeMap.get("Baby").equals("Yes")) {
            zombie.setBaby(true);
        } else {
            zombie.setBaby(false);
        }
    }
    
    private void pigZombie(PigZombie pigZombie) {
        pigZombie.setAnger(Integer.parseInt(attributeMap.get("Anger Level")));
    }
    
    private void zombieVillager(ZombieVillager zombieVillager) {
        zombieVillager.setVillagerProfession(Profession.valueOf(attributeMap.get("Profession")));
    }
    
    private void evoker(Evoker evoker) {
        evoker.setCurrentSpell(Spell.valueOf(attributeMap.get("Active Spell")));
    }
    
    private void ironGolem(IronGolem ironGolem) {
        String playerCreatedStr = attributeMap.get("Player Created");
        
        if (playerCreatedStr.equals("Yes")) {
            ironGolem.setPlayerCreated(true);
        } else {
            ironGolem.setPlayerCreated(false);
        }
    }
    
    private void snowman(Snowman snowman) {
        String derpStr = attributeMap.get("Derp");
        
        if (derpStr.equals("Yes")) {
            snowman.setDerp(true);
        } else {
            snowman.setDerp(false);
        }
    }
}
