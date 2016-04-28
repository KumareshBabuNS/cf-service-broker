/// **
// *
// */
// package de.evoila.cf.cpi.openstack.custom;
//
// import java.io.IOException;
// import java.net.URISyntaxException;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.UUID;
//
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.Test;
// import org.openstack4j.model.heat.Stack;
// import org.springframework.beans.factory.annotation.Autowired;
//
// import de.evoila.cf.broker.exception.PlatformException;
// import de.evoila.cf.broker.model.Plan;
// import de.evoila.cf.broker.model.Platform;
// import de.evoila.cf.broker.model.ServiceInstance;
// import de.evoila.cf.broker.model.VolumeUnit;
// import de.evoila.cf.cpi.BaseIntegrationTest;
// import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
// import de.evoila.cf.cpi.openstack.util.StackProgressObserver;
//
/// **
// * @author Johannes Hiemer.
// *
// */
// public class OpenstackPlatformServiceClusterTest extends BaseIntegrationTest
/// {
//
// private Plan plan;
//
// private ServiceInstance serviceInstance;
//
// @Autowired
// private HeatFluent heatFluent;
//
// @Autowired
// private StackProgressObserver stackProgressObserver;
//
// @Autowired
// private StackHandler stackHandler;
//
// @Before
// public void before() {
// plan = new Plan("basic", "500 MB PostgreSQL DB Basic Instance",
// "The most basic PostgreSQL plan currently available. Providing"
// + "500 MB of capcity in a PostgreSQL DB.", Platform.OPENSTACK, 25,
/// VolumeUnit.M, "3", 10);
//
// serviceInstance = new ServiceInstance(UUID.randomUUID().toString(),
// UUID.randomUUID().toString(), plan.getId(),
// UUID.randomUUID().toString(), UUID.randomUUID().toString(),
// null, "http://currently.not/available");
//
// Assert.assertNotNull(serviceInstance);
// }
//
// @Test
// public void createCluster() throws IOException, URISyntaxException,
/// PlatformException, InterruptedException {
// final String uuid = UUID.randomUUID().toString();
// final Integer numberSecondaries = 3;
// String templatePorts =
/// stackHandler.accessTemplate("/openstack/templatePorts.yaml");
// String namePorts = "testResourceGroupsForNeutronPorts"+uuid;
//
// Integer numberPorts = numberSecondaries+1;
// Map<String, String> parametersPorts = new HashMap<String, String>();
// parametersPorts.put("port_number", numberPorts.toString());
// parametersPorts.put("network_id", "3e73c0d4-31a8-4cb5-a2f8-b12e4577395c");
//
// heatFluent.create(namePorts, templatePorts, parametersPorts , false, 10l);
//
// Stack stackPorts = stackProgressObserver.waitForStackCompletion(namePorts);
//
//
// List<String> ips = null;
// List<String> ports = null;
//
// for (Map<String, Object> output : stackPorts.getOutputs()) {
// Object outputKey = output.get("output_key");
// if (outputKey != null && outputKey instanceof String) {
// String key = (String) outputKey;
// if (key.equals("secondary_ips")) {
// ips = (List<String>) output.get("output_value");
//
// }
// if (key.equals("secondary_ports")) {
// ports = (List<String>) output.get("output_value");
// }
// }
// }
//
// String primIp = ips.get(0);
// ips.remove(0);
// String primPort = ports.get(0);
// ports.remove(0);
// String primHostname = "p-"+primIp.replace(".", "-");
//
// String etcHosts = primIp+" "+primHostname+"\n";
// for (String secIp : ips) {
// etcHosts += secIp+" "+"sec-"+secIp.replace(".", "-")+"\n";
// }
//
// System.out.println(etcHosts);
//
// String templatePrimary =
/// stackHandler.accessTemplate("/openstack/templatePrim.yaml");
// String namePrimary = "testResourceGroupPrimary"+uuid;
//
//
// Map<String, String> parametersPrimary = new HashMap<String, String>();
// parametersPrimary.put("rabbit_vhost", "evoila");
// parametersPrimary.put("rabbit_user", "evoila");
// parametersPrimary.put("rabbit_password", "evoila");
// parametersPrimary.put("log_host", "172.24.102.12");
// parametersPrimary.put("log_port", "5002");
// parametersPrimary.put("erlang_key", "thisisjustatest4usguysfromEVOILA");
// parametersPrimary.put("flavor", "m1.small");
// parametersPrimary.put("volume_size", "2");
// parametersPrimary.put("etcHosts", etcHosts);
// parametersPrimary.put("masterHostname", primHostname);
// parametersPrimary.put("port_prim", primPort);
// parametersPrimary.put("availability_zone", "nova");
// parametersPrimary.put("keypair", "cmueller");
// parametersPrimary.put("image_id", "b89dd034-f4f0-4a2a-95bd-74f325428f07");
//
// System.out.println(parametersPrimary.toString());
//
// heatFluent.create(namePrimary, templatePrimary, parametersPrimary , false,
/// 10l);
//
// stackProgressObserver.waitForStackCompletion(namePrimary);
//
//
//
// String templateSec =
/// stackHandler.accessTemplate("/openstack/templateSecondaries.yaml");
// String nameSec = "testResourceGroupSecondary"+uuid;
//
//
// Map<String, String> parametersSec = new HashMap<String, String>();
// parametersSec.put("rabbit_vhost", "evoila");
// parametersSec.put("rabbit_user", "evoila");
// parametersSec.put("rabbit_password", "evoila");
// parametersSec.put("log_host", "172.24.102.12");
// parametersSec.put("log_port", "5002");
// parametersSec.put("erlang_key", "thisisjustatest4usguysfromEVOILA");
// parametersSec.put("flavor", "m1.small");
// parametersSec.put("volume_size", "2");
// parametersSec.put("etcHosts", etcHosts);
// parametersSec.put("availability_zone", "nova");
// parametersSec.put("keypair", "cmueller");
// parametersSec.put("image_id", "b89dd034-f4f0-4a2a-95bd-74f325428f07");
// parametersSec.put("masterHostname", primHostname);
//
//
// for (int i = 0; i < numberSecondaries; i++) {
// if ( i > 0 ) Thread.sleep(1000);
// if (parametersSec.containsKey("network_port")) {
// parametersSec.remove("network_port");
// }
// parametersSec.put("network_port", ports.get(i));
//
// if (parametersSec.containsKey("secondaryHostname")) {
// parametersSec.remove("secondaryHostname");
// }
// parametersSec.put("secondaryHostname", "sec-"+ips.get(i).replace(".", "-"));
//
// System.out.println("Create Sec Nr."+i);
// System.out.println(parametersSec.toString());
// System.out.println(nameSec+"_"+i);
// heatFluent.create(nameSec+"_"+i, templateSec, parametersSec , false, 10l);
// System.out.println("called");
//
// }
//
// List<Stack> stackSec = new ArrayList<Stack>();
// for (int i = 0; i < numberSecondaries; i++) {
// System.out.println("wait for Secondaries No. "+i);
// stackSec.add(stackProgressObserver.waitForStackCompletion(nameSec+"_"+i));
// }
//
// for (int i = 0; i < numberSecondaries; i++) {
// Assert.assertNotNull(stackSec.get(i));
// }
//
// }
//
//
// }
