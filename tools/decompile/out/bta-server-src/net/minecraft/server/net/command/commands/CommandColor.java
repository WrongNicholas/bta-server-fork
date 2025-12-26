package net.minecraft.server.net.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.IServerCommandSource;

public class CommandColor implements CommandManager.CommandRegistry {
   @Override
   public void register(CommandDispatcher<CommandSource> dispatcher) {
      CommandNode<CommandSource> command = dispatcher.register(
         (ArgumentBuilderLiteral<CommandSource>)((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("color")
               .then(((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("get").executes(c -> {
                  CommandSource source = (CommandSource)c.getSource();
                  if (!(source instanceof IServerCommandSource)) {
                     throw CommandExceptions.multiplayerWorldOnly().create();
                  } else {
                     PlayerServer player = (PlayerServer)source.getSender();
                     if (player == null) {
                        throw CommandExceptions.notInWorld().create();
                     } else {
                        TextFormatting color = TextFormatting.get(player.chatColor);
                        source.sendMessage(I18n.getInstance().translateKeyAndFormat("command.commands.color.get.success", color + color.getNames()[0]));
                        return 1;
                     }
                  }
               })).then(ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.username()).executes(c -> {
                  CommandSource source = (CommandSource)c.getSource();
                  if (!(source instanceof IServerCommandSource)) {
                     throw CommandExceptions.multiplayerWorldOnly().create();
                  } else {
                     EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);
                     PlayerServer player = (PlayerServer)entitySelector.get(source).get(0);
                     TextFormatting color = TextFormatting.get(player.chatColor);
                     source.sendMessage(I18n.getInstance().translateKeyAndFormat("command.commands.color.get.success_other", color + color.getNames()[0]));
                     return 1;
                  }
               }))))
            .then(
               ((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal(
                                                                     "set"
                                                                  )
                                                                  .then(ArgumentBuilderLiteral.literal("white").executes(c -> {
                                                                     CommandSource source = (CommandSource)c.getSource();
                                                                     return setColor((PlayerServer)source.getSender(), source, (byte)0);
                                                                  })))
                                                               .then(ArgumentBuilderLiteral.literal("orange").executes(c -> {
                                                                  CommandSource source = (CommandSource)c.getSource();
                                                                  return setColor((PlayerServer)source.getSender(), source, (byte)1);
                                                               })))
                                                            .then(ArgumentBuilderLiteral.literal("magenta").executes(c -> {
                                                               CommandSource source = (CommandSource)c.getSource();
                                                               return setColor((PlayerServer)source.getSender(), source, (byte)2);
                                                            })))
                                                         .then(ArgumentBuilderLiteral.literal("light_blue").executes(c -> {
                                                            CommandSource source = (CommandSource)c.getSource();
                                                            return setColor((PlayerServer)source.getSender(), source, (byte)3);
                                                         })))
                                                      .then(ArgumentBuilderLiteral.literal("yellow").executes(c -> {
                                                         CommandSource source = (CommandSource)c.getSource();
                                                         return setColor((PlayerServer)source.getSender(), source, (byte)4);
                                                      })))
                                                   .then(ArgumentBuilderLiteral.literal("lime").executes(c -> {
                                                      CommandSource source = (CommandSource)c.getSource();
                                                      return setColor((PlayerServer)source.getSender(), source, (byte)5);
                                                   })))
                                                .then(ArgumentBuilderLiteral.literal("pink").executes(c -> {
                                                   CommandSource source = (CommandSource)c.getSource();
                                                   return setColor((PlayerServer)source.getSender(), source, (byte)6);
                                                })))
                                             .then(ArgumentBuilderLiteral.literal("gray").executes(c -> {
                                                CommandSource source = (CommandSource)c.getSource();
                                                return setColor((PlayerServer)source.getSender(), source, (byte)7);
                                             })))
                                          .then(ArgumentBuilderLiteral.literal("light_gray").executes(c -> {
                                             CommandSource source = (CommandSource)c.getSource();
                                             return setColor((PlayerServer)source.getSender(), source, (byte)8);
                                          })))
                                       .then(ArgumentBuilderLiteral.literal("cyan").executes(c -> {
                                          CommandSource source = (CommandSource)c.getSource();
                                          return setColor((PlayerServer)source.getSender(), source, (byte)9);
                                       })))
                                    .then(ArgumentBuilderLiteral.literal("purple").executes(c -> {
                                       CommandSource source = (CommandSource)c.getSource();
                                       return setColor((PlayerServer)source.getSender(), source, (byte)10);
                                    })))
                                 .then(ArgumentBuilderLiteral.literal("blue").executes(c -> {
                                    CommandSource source = (CommandSource)c.getSource();
                                    return setColor((PlayerServer)source.getSender(), source, (byte)11);
                                 })))
                              .then(ArgumentBuilderLiteral.literal("brown").executes(c -> {
                                 CommandSource source = (CommandSource)c.getSource();
                                 return setColor((PlayerServer)source.getSender(), source, (byte)12);
                              })))
                           .then(ArgumentBuilderLiteral.literal("green").executes(c -> {
                              CommandSource source = (CommandSource)c.getSource();
                              return setColor((PlayerServer)source.getSender(), source, (byte)13);
                           })))
                        .then(ArgumentBuilderLiteral.literal("red").executes(c -> {
                           CommandSource source = (CommandSource)c.getSource();
                           return setColor((PlayerServer)source.getSender(), source, (byte)14);
                        })))
                     .then(ArgumentBuilderLiteral.literal("black").executes(c -> {
                        CommandSource source = (CommandSource)c.getSource();
                        return setColor((PlayerServer)source.getSender(), source, (byte)15);
                     })))
                  .then(
                     ((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)((ArgumentBuilderRequired)ArgumentBuilderRequired.<CommandSource, EntitySelector>argument(
                                                                           "target", ArgumentTypeEntity.usernames()
                                                                        )
                                                                        .requires(CommandSource::hasAdmin))
                                                                     .then(ArgumentBuilderLiteral.literal("white").executes(c -> {
                                                                        CommandSource source = (CommandSource)c.getSource();
                                                                        EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                                        for (Entity entity : entitySelector.get(source)) {
                                                                           setColor((PlayerServer)entity, source, (byte)0);
                                                                        }

                                                                        return 1;
                                                                     })))
                                                                  .then(ArgumentBuilderLiteral.literal("orange").executes(c -> {
                                                                     CommandSource source = (CommandSource)c.getSource();
                                                                     EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                                     for (Entity entity : entitySelector.get(source)) {
                                                                        setColor((PlayerServer)entity, source, (byte)1);
                                                                     }

                                                                     return 1;
                                                                  })))
                                                               .then(ArgumentBuilderLiteral.literal("magenta").executes(c -> {
                                                                  CommandSource source = (CommandSource)c.getSource();
                                                                  EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                                  for (Entity entity : entitySelector.get(source)) {
                                                                     setColor((PlayerServer)entity, source, (byte)2);
                                                                  }

                                                                  return 1;
                                                               })))
                                                            .then(ArgumentBuilderLiteral.literal("light_blue").executes(c -> {
                                                               CommandSource source = (CommandSource)c.getSource();
                                                               EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                               for (Entity entity : entitySelector.get(source)) {
                                                                  setColor((PlayerServer)entity, source, (byte)3);
                                                               }

                                                               return 1;
                                                            })))
                                                         .then(ArgumentBuilderLiteral.literal("yellow").executes(c -> {
                                                            CommandSource source = (CommandSource)c.getSource();
                                                            EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                            for (Entity entity : entitySelector.get(source)) {
                                                               setColor((PlayerServer)entity, source, (byte)4);
                                                            }

                                                            return 1;
                                                         })))
                                                      .then(ArgumentBuilderLiteral.literal("lime").executes(c -> {
                                                         CommandSource source = (CommandSource)c.getSource();
                                                         EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                         for (Entity entity : entitySelector.get(source)) {
                                                            setColor((PlayerServer)entity, source, (byte)5);
                                                         }

                                                         return 1;
                                                      })))
                                                   .then(ArgumentBuilderLiteral.literal("pink").executes(c -> {
                                                      CommandSource source = (CommandSource)c.getSource();
                                                      EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                      for (Entity entity : entitySelector.get(source)) {
                                                         setColor((PlayerServer)entity, source, (byte)6);
                                                      }

                                                      return 1;
                                                   })))
                                                .then(ArgumentBuilderLiteral.literal("gray").executes(c -> {
                                                   CommandSource source = (CommandSource)c.getSource();
                                                   EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                   for (Entity entity : entitySelector.get(source)) {
                                                      setColor((PlayerServer)entity, source, (byte)7);
                                                   }

                                                   return 1;
                                                })))
                                             .then(ArgumentBuilderLiteral.literal("light_gray").executes(c -> {
                                                CommandSource source = (CommandSource)c.getSource();
                                                EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                                for (Entity entity : entitySelector.get(source)) {
                                                   setColor((PlayerServer)entity, source, (byte)8);
                                                }

                                                return 1;
                                             })))
                                          .then(ArgumentBuilderLiteral.literal("cyan").executes(c -> {
                                             CommandSource source = (CommandSource)c.getSource();
                                             EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                             for (Entity entity : entitySelector.get(source)) {
                                                setColor((PlayerServer)entity, source, (byte)9);
                                             }

                                             return 1;
                                          })))
                                       .then(ArgumentBuilderLiteral.literal("purple").executes(c -> {
                                          CommandSource source = (CommandSource)c.getSource();
                                          EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                          for (Entity entity : entitySelector.get(source)) {
                                             setColor((PlayerServer)entity, source, (byte)10);
                                          }

                                          return 1;
                                       })))
                                    .then(ArgumentBuilderLiteral.literal("blue").executes(c -> {
                                       CommandSource source = (CommandSource)c.getSource();
                                       EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                       for (Entity entity : entitySelector.get(source)) {
                                          setColor((PlayerServer)entity, source, (byte)11);
                                       }

                                       return 1;
                                    })))
                                 .then(ArgumentBuilderLiteral.literal("brown").executes(c -> {
                                    CommandSource source = (CommandSource)c.getSource();
                                    EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                    for (Entity entity : entitySelector.get(source)) {
                                       setColor((PlayerServer)entity, source, (byte)12);
                                    }

                                    return 1;
                                 })))
                              .then(ArgumentBuilderLiteral.literal("green").executes(c -> {
                                 CommandSource source = (CommandSource)c.getSource();
                                 EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                                 for (Entity entity : entitySelector.get(source)) {
                                    setColor((PlayerServer)entity, source, (byte)13);
                                 }

                                 return 1;
                              })))
                           .then(ArgumentBuilderLiteral.literal("red").executes(c -> {
                              CommandSource source = (CommandSource)c.getSource();
                              EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                              for (Entity entity : entitySelector.get(source)) {
                                 setColor((PlayerServer)entity, source, (byte)14);
                              }

                              return 1;
                           })))
                        .then(ArgumentBuilderLiteral.literal("black").executes(c -> {
                           CommandSource source = (CommandSource)c.getSource();
                           EntitySelector entitySelector = c.getArgument("target", EntitySelector.class);

                           for (Entity entity : entitySelector.get(source)) {
                              setColor((PlayerServer)entity, source, (byte)15);
                           }

                           return 1;
                        }))
                  )
            )
      );
      dispatcher.register(ArgumentBuilderLiteral.<CommandSource>literal("colour").redirect(command));
   }

   private static int setColor(PlayerServer player, CommandSource source, byte colorId) throws CommandSyntaxException {
      if (!(source instanceof IServerCommandSource)) {
         throw CommandExceptions.multiplayerWorldOnly().create();
      } else if (player == null) {
         throw CommandExceptions.notInWorld().create();
      } else {
         player.chatColor = colorId;
         TextFormatting color = TextFormatting.get(colorId);
         if (player == source.getSender()) {
            source.sendTranslatableMessage("command.commands.color.set.success", color + color.getNames()[0]);
         } else {
            source.sendTranslatableMessage("command.commands.color.set.success_other", CommandHelper.getEntityName(player), color + color.getNames()[0]);
            source.sendTranslatableMessage(player, "command.commands.color.set.success_receiver", color + color.getNames()[0]);
         }

         return 1;
      }
   }
}
