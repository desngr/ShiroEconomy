package me.desngr.util;

import lombok.experimental.UtilityClass;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@UtilityClass
public class PermissionUtil {

    public CompletableFuture<List<User>> getUsersInGroup(String groupName) {
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(groupName)
                .build());

        return LuckPermsProvider.get()
                .getUserManager()
                .searchAll(matcher)
                .thenComposeAsync(results -> {
            List<CompletableFuture<User>> users = new ArrayList<>();

            return CompletableFuture.allOf(
                    results.keySet().stream()
                            .map(uuid -> LuckPermsProvider.get().getUserManager()
                                    .loadUser(uuid))
                            .peek(users::add)
                            .toArray(CompletableFuture[]::new)
            ).thenApply(x -> users.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList())
            );
        });
    }
}
