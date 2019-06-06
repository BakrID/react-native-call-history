declare module 'react-native-call-history' {
	export function getCallHistory(startDate: string, endDate: string): Promise<{number: string, type: string, date: string, rawDate: string, duration: string}[]>;
}
