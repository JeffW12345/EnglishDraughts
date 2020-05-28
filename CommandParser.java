
/*
 * Used for parsing inbound messages, producing an array of CommandParser instances
 * that represent each element in the original comma delimited message
 */
public class CommandParser {

	public static CommandParser[] parseFromMessage(String inputMsgFromClient) {
		inputMsgFromClient = inputMsgFromClient.trim().toLowerCase();
		var parsed = inputMsgFromClient.split(",");
		CommandParser pieces[] = new CommandParser[parsed.length];
		for (int i = 0; i < parsed.length; i++) {
			pieces[i] = new CommandParser(parsed[i]);
		}
		return pieces;
	}

	public String part;

	public CommandParser(String bit) {
		part = bit.trim().toLowerCase();
	}

	public Integer asInt() {
		return Integer.valueOf(part);
	}

	@Override
	public String toString() {
		return part;
	}

}
