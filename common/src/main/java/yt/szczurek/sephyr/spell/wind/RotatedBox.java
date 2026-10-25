package yt.szczurek.sephyr.spell.wind;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Intersectionf;
import org.joml.Vector3fc;

public record RotatedBox(Vector3fc center,
        float diameter,
        float depth,
        float angleX,
        float angleY
) {
    public static Codec<RotatedBox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vec3.CODEC.fieldOf("Center").xmap(vec3 -> (Vector3fc) vec3.toVector3f(), Vec3::new).forGetter(RotatedBox::center),
            Codec.FLOAT.fieldOf("diameter").forGetter(RotatedBox::diameter),
            Codec.FLOAT.fieldOf("depth").forGetter(RotatedBox::depth),
            Codec.FLOAT.fieldOf("angleX").forGetter(RotatedBox::angleX),
            Codec.FLOAT.fieldOf("angleY").forGetter(RotatedBox::angleY)
    ).apply(instance, RotatedBox::new));

    public AABB aabb() {
        float halfDiameter = diameter * 0.5f;
        float halfDepth = depth * 0.5f;

        float ySin = (float) Math.sin(angleY);
        float yCos = (float) Math.cos(angleY);
        float xSin = (float) Math.sin(angleX);
        float xCos = (float) Math.cos(angleX);
        
        float halfExtentX =
                  Math.abs(yCos) * halfDiameter
                + Math.abs(ySin * xSin) * halfDiameter
                + Math.abs(ySin * xCos) * halfDepth;

        float halfExtentY =
                  Math.abs(xCos) * halfDiameter
                + Math.abs(xSin) * halfDepth;

        float halfExtentZ =
                  Math.abs(ySin) * halfDiameter
                + Math.abs(yCos * xSin) * halfDiameter
                + Math.abs(yCos * xCos) * halfDepth;

        return new AABB(
                center.x() - halfExtentX,
                center.y() - halfExtentY,
                center.z() - halfExtentZ,
                center.x() + halfExtentX,
                center.y() + halfExtentY,
                center.z() + halfExtentZ
        );
    }

    public boolean intersectAABB(AABB aabb) {
        float halfDiameter = diameter * 0.5f;
        float halfDepth  = depth * 0.5f;

        float aabbCenterX = (float) (aabb.minX + aabb.maxX) * 0.5f;
        float aabbCenterY = (float) (aabb.minY + aabb.maxY) * 0.5f;
        float aabbCenterZ = (float) (aabb.minZ + aabb.maxZ) * 0.5f;

        float aabbHalfX = (float) (aabb.maxX - aabb.minX) * 0.5f;
        float aabbHalfY = (float) (aabb.maxY - aabb.minY) * 0.5f;
        float aabbHalfZ = (float) (aabb.maxZ - aabb.minZ) * 0.5f;

        float cosYaw   = (float) Math.cos(-angleY);
        float sinYaw   = (float) Math.sin(-angleY);
        float cosPitch = (float) Math.cos(angleX);
        float sinPitch = (float) Math.sin(angleX);

        return Intersectionf.testObOb(
                aabbCenterX, aabbCenterY, aabbCenterZ,
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f,
                aabbHalfX, aabbHalfY, aabbHalfZ,
                center.x(), center.y(), center.z(),
                cosYaw,              0f,  -sinYaw,
                sinYaw * sinPitch,   cosPitch,  cosYaw * sinPitch,
                sinYaw * cosPitch,  -sinPitch,  cosYaw * cosPitch,
                halfDiameter, halfDiameter, halfDepth
        );
    }
}
