export interface IGoal {
    id?: number;
    minutes?: number;
}

export class Goal implements IGoal {
    constructor(public id?: number, public minutes?: number) {}
}
