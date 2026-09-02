package org.example;
import java.io.File;
import java.util.Locale;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.LofOffset;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvider;
import org.orekit.data.DirectoryCrawler;
import org.orekit.forces.maneuvers.ImpulseManeuver;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.LOFType;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.*;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateComponents;
import org.orekit.time.TimeComponents;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

public class maneuevers {
    public static void maneuevers(String[] args){
        final Frame eme2000 = FramesFactory.getEME2000();
        final Orbit initialOrbit = new KeplerianOrbit(8000000.0, 0.01, FastMath.toRadians(50.0), // ← this is initial inclination
                    FastMath.toRadians(140.0),
                    FastMath.toRadians(12.0),
                    FastMath.toRadians(-60.0), PositionAngleType.MEAN,
                    eme2000,
                    new AbsoluteDate(new DateComponents(2008, 6, 23),
                            new TimeComponents(14, 0, 0),
                            TimeScalesFactory.getUTC()),
                    Constants.EIGEN5C_EARTH_MU);
    final AttitudeProvider attitudeProvider = new LofOffset(eme2000, LOFType.LVLH);
    final EventDetector ascendingNodeDetector = new EventSlopeFilter<>(
            new NodeDetector(FramesFactory.getEME2000()).
                    withMaxCheck(300.0).
                    withThreshold(1.0e-6),
            FilterType.TRIGGER_ONLY_INCREASING_EVENTS);
    final AbsoluteDate lastAllowedDate =
            initialOrbit.getDate().shiftedBy(3 * initialOrbit.getKeplerianPeriod());
    final EnablingPredicate predicate =
            (state, detector, g) -> state.getDate().isBefore(lastAllowedDate);
    final EventDetector trigger =
            new EventEnablingPredicateFilter(ascendingNodeDetector, predicate);
    final ImpulseManeuver maneuver = new ImpulseManeuver(trigger, new Vector3D(0.0, 0.0, -122.25),350.0); // ← 122.25 m/s along -Z
    final KeplerianPropagator propagator = new KeplerianPropagator(initialOrbit, attitudeProvider);
    propagator.addEventDetector(maneuver);
        propagator.getMultiplexer().add(900.0, (state) -> {
            final Vector3D pos = state.getPVCoordinates(eme2000).getPosition();
            System.out.format(Locale.US, "%s %s hemisphere inclination = %5.3f%n",
                    state.getDate(),
                    pos.getZ() > 0 ? "Northern" : "Southern",
                    FastMath.toDegrees(state.getOrbit().getI()));
        });
        propagator.propagate(initialOrbit.getDate().shiftedBy(5 * initialOrbit.getKeplerianPeriod()));
    }
}
