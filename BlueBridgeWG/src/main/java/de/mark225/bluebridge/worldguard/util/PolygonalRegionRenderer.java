package de.mark225.bluebridge.worldguard.util;

import com.flowpowered.math.vector.Vector2d;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PolygonalRegionRenderer {

    public static List<Vector2d> render(ProtectedRegion region) {
        var points = region.getPoints();
        var result = new ArrayList<Vector2d>(points.size());

        for (var point : expandPolygonXZByOne(points)) {
            result.add(new Vector2d(point.getX(), point.getZ()));
        }

        return result;
    }

    // Original: https://github.com/okocraft/Dynmap-WorldGuard/blob/master/src/main/java/org/dynmap/worldguard/UpdateTask.java#L146-L241

    private static double cross(BlockVector2 p1, BlockVector2 p2) {
        return p1.getX() * p2.getZ() - p1.getZ() * p2.getX();
    }

    private static double calcAreaOfPolygon(List<BlockVector2> points) {
        double area = 0;
        for (int i = 0; i < points.size(); i++) {
            area += cross(points.get(i), points.get((i + 1) % points.size()));
        }
        return area / 2.0;
    }

    /**
     * Calc loop direction of given polygon.
     *
     * @param points Polygon points.
     * @return When returns 1 it is clockwise, when returns -1 it is anticlockwise.
     * Other than that, polygon is collapsed.
     */
    private static int getPolygonLoop(List<BlockVector2> points) {
        double area = calcAreaOfPolygon(points);
        if (area > 0) {
            return 1;
        } else if (area < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    private static List<BlockVector2> expandPolygonXZByOne(List<BlockVector2> points) {
        List<BlockVector2> pointsCopy = new ArrayList<>(points);
        if (points.size() < 3) {
            return pointsCopy;
        }

        List<BlockVector2> result = new ArrayList<>();
        int loop = getPolygonLoop(points);
        if (loop == 0) {
            Polygonal2DRegion poly2d = new Polygonal2DRegion(null, points, 0, 0);
            BlockVector2 max = poly2d.getMaximumPoint().toBlockVector2();
            BlockVector2 min = poly2d.getMinimumPoint().toBlockVector2();
            if (min.getBlockX() == max.getBlockX()) {
                result.add(min);
                result.add(max.add(0, 1));
                result.add(max.add(1, 1));
                result.add(min.add(1, 0));
            } else {
                result.add(min);
                result.add(max.add(1, 0));
                result.add(max.add(1, 1));
                result.add(min.add(0, 1));
            }
            return result;
        }
        if (loop != 1) {
            Collections.reverse(pointsCopy);
        }

        List<BlockVector2> pointAdded = new ArrayList<>();
        for (int i = 0; i < pointsCopy.size(); i++) {
            BlockVector2 prev = pointsCopy.get((i - 1 + pointsCopy.size()) % pointsCopy.size());
            BlockVector2 cur = pointsCopy.get(i);
            BlockVector2 next = pointsCopy.get((i + 1) % pointsCopy.size());
            pointAdded.add(cur);
            if (cross(cur.subtract(prev), next.subtract(cur)) == 0 && cur.subtract(prev).dot(next.subtract(cur)) < 0) {
                pointAdded.add(cur);
            }
        }
        pointsCopy = pointAdded;

        for (int i = 0; i < pointsCopy.size(); i++) {
            BlockVector2 prev = pointsCopy.get((i - 1 + pointsCopy.size()) % pointsCopy.size());
            BlockVector2 cur = pointsCopy.get(i);
            BlockVector2 next = pointsCopy.get((i + 1) % pointsCopy.size());
            int xPrev = prev.getX();
            int zPrev = prev.getZ();
            int xCur = cur.getX();
            int zCur = cur.getZ();
            int xNext = next.getX();
            int zNext = next.getZ();

            int xCurNew = xCur;
            int zCurNew = zCur;

            if (zPrev < zCur || zCur < zNext || cur.equals(next) && xPrev < xCur || prev.equals(cur) && xNext < xCur) {
                xCurNew++;
            }
            if (xCur < xPrev || xNext < xCur || cur.equals(next) && zPrev < zCur || prev.equals(cur) && zNext < zCur) {
                zCurNew++;
            }

            result.add(BlockVector2.at(xCurNew, zCurNew));
        }
        return result;
    }

    private PolygonalRegionRenderer() {
        throw new UnsupportedOperationException();
    }
}
