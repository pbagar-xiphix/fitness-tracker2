export interface IActivity {
    id?: number;
    desc?: string;
    exerciseId?: number;
}

export class Activity implements IActivity {
    constructor(public id?: number, public desc?: string, public exerciseId?: number) {}
}
