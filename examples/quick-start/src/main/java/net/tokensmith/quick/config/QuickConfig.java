package net.tokensmith.quick.config;

import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.Configure;
import net.tokensmith.otter.gateway.Gateway;
import net.tokensmith.otter.gateway.builder.GroupBuilder;
import net.tokensmith.otter.gateway.builder.ShapeBuilder;
import net.tokensmith.otter.gateway.builder.TargetBuilder;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.entity.Target;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.quick.controller.HelloResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuickConfig implements Configure {
    public static final String WEB_SITE_GROUP = "WebSite";

    @Override
    public Shape shape() {

        // You should vault your keys, this is shown for simplicity.
        SymmetricKey encKey = new SymmetricKey(
                Optional.of("key-2"),
                "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
                Use.ENCRYPTION
        );

        // You should vault your keys, this is shown for simplicity.
        SymmetricKey signKey = new SymmetricKey(
            Optional.of("key-1"),
            "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
            Use.SIGNATURE);

        return new ShapeBuilder()
                .encKey(encKey)
                .signkey(signKey)
                .build();
    }

    @Override
    public List<Group<? extends DefaultSession, ? extends DefaultUser>> groups() {
        List<Group<? extends DefaultSession, ? extends DefaultUser>> groups = new ArrayList<>();

        Group<DefaultSession, DefaultUser> webSiteGroup = new GroupBuilder<>()
                .name(WEB_SITE_GROUP)
                .sessionClazz(DefaultSession.class)
                .build();

        groups.add(webSiteGroup);

        return groups;
    }

    @Override
    public List<RestGroup<? extends DefaultSession, ? extends DefaultUser>> restGroups() {
        return new ArrayList<>();
    }

    @Override
    public void routes(Gateway gateway) {
        Target<DefaultSession, DefaultUser> hello = new TargetBuilder<>()
                .groupName(WEB_SITE_GROUP)
                .method(Method.GET)
                .resource(new HelloResource())
                .regex(HelloResource.URL)
                .build();

        gateway.add(hello);
    }
}
