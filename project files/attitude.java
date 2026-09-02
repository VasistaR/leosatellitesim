//package org.example;
//
//import org.hipparchus.geometry.euclidean.threed.RotationOrder;
//import org.hipparchus.geometry.euclidean.threed.Vector3D;
//import org.hipparchus.util.FastMath;
//import org.orekit.attitudes.AttitudeProvider;
//import org.orekit.attitudes.AttitudeSwitchHandler;
//import org.orekit.attitudes.AttitudesSequence;
//import org.orekit.attitudes.LofOffset;
//import org.orekit.bodies.CelestialBody;
//import org.orekit.bodies.CelestialBodyFactory;
//import org.orekit.bodies.OneAxisEllipsoid;
//import org.orekit.files.ccsds.ndm.adm.apm.AngularVelocity;
//import org.orekit.frames.FramesFactory;
//import org.orekit.frames.LOFType;
//import org.orekit.propagation.Propagator;
//import org.orekit.propagation.SpacecraftState;
//import org.orekit.propagation.analytical.EcksteinHechlerPropagator;
//import org.orekit.propagation.events.EclipseDetector;
//import org.orekit.propagation.events.EventDetector;
//import org.orekit.propagation.events.handlers.ContinueOnEvent;
//import org.orekit.time.TimeScale;
//import org.orekit.utils.AngularDerivativesFilter;
//import org.orekit.utils.Constants;
//import org.orekit.utils.IERSConventions;
//
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;
//import java.util.Locale;
//import java.util.SortedSet;
//import java.util.TreeSet;
//
//public class attitude {
//    public static void attitude(String[] args){
//        final SortedSet<String> output = new TreeSet<>();
//        AngularVelocity initialOrbit = null;
//        final AttitudeProvider dayObservationLaw = new LofOffset(initialOrbit.getFrame().asFrame(), LOFType.VVLH, RotationOrder.XYZ, FastMath.toRadians(20), FastMath.toRadians(40), 0);
//        final AttitudeProvider nightRestingLaw = new LofOffset(initialOrbit.getFrame().asFrame(), LOFType.VVLH);
//        CelestialBody sun   = CelestialBodyFactory.getSun();
//        OneAxisEllipsoid earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, 0.0, FramesFactory.getITRF(IERSConventions.IERS_2010, true));
//        EventDetector dayNightEvent = new EclipseDetector(sun, 696000000., earth).withHandler(new ContinueOnEvent());
//        EventDetector nightDayEvent = new EclipseDetector(sun, 696000000., earth).withHandler(new ContinueOnEvent());
//        AttitudesSequence attitudesSequence = new AttitudesSequence();
//        TimeScale utc;
//        AttitudeSwitchHandler switchHandler = (preceding, following, s) -> {
//                    if (preceding == dayObservationLaw) {
//                        output.add(s.getDate().toStringWithoutUtcOffset(utc, 3) + ": switching to night law");
//                    }
//                    else {
//                        output.add(s.getDate().toStringWithoutUtcOffset(utc, 3) + ": switching to day law");
//                    }
//                };
//        attitudesSequence.addSwitchingCondition(dayObservationLaw, nightRestingLaw, dayNightEvent, false, true, 10.0, AngularDerivativesFilter.USE_R, switchHandler);
//        attitudesSequence.addSwitchingCondition(nightRestingLaw, dayObservationLaw, nightDayEvent, true, false, 10.0, AngularDerivativesFilter.USE_R, switchHandler);
//        if (dayNightEvent.g(new SpacecraftState(initialOrbit)) >= 0) {
//            // initial position is in daytime
//            attitudesSequence.resetActiveProvider(dayObservationLaw);
//        }
//        else {
//            // initial position is in nighttime
//            attitudesSequence.resetActiveProvider(nightRestingLaw);
//        }
//        Propagator propagator = new EcksteinHechlerPropagator(initialOrbit, attitudesSequence, Constants.EIGEN5C_EARTH_EQUATORIAL_RADIUS, Constants.EIGEN5C_EARTH_MU, Constants.EIGEN5C_EARTH_C20, Constants.EIGEN5C_EARTH_C30, Constants.EIGEN5C_EARTH_C40, Constants.EIGEN5C_EARTH_C50, Constants.EIGEN5C_EARTH_C60);
//        propagator.getMultiplexer().add(180.0, (currentState, isLast) -> {
//            DecimalFormatSymbols angleDegree = new DecimalFormatSymbols(Locale.US);
//            angleDegree.setDecimalSeparator('\u00b0');
//            DecimalFormat ad = new DecimalFormat(" 00.000;-00.000", angleDegree);
//
//            // the Earth position in spacecraft frame should be along spacecraft Z axis
//            // during nigthtime and away from it during daytime due to roll and pitch offsets
//            final Vector3D earthDir = currentState.toTransform().transformPosition(Vector3D.ZERO);
//            final double pointingOffset = Vector3D.angle(earthDir, Vector3D.PLUS_K);
//
//            // the g function is the eclipse indicator, it is an angle between Sun and Earth limb,
//            // positive when Sun is outside of Earth limb, negative when Sun is hidden by Earth limb
//            final double eclipseAngle = dayNightEvent.g(currentState);
//
//            output.add(currentState.getDate().toStringWithoutUtcOffset(utc, 3) +
//                    " " + ad.format(FastMath.toDegrees(eclipseAngle) +
//                    " " + ad.format(FastMath.toDegrees(pointingOffset))));
//        });
//        SpacecraftState finalState = propagator.propagate(initialDate.shiftedBy(12600.));
//        for (final String line : output) {
//            System.out.println(line);
//        }
//    }
//}
